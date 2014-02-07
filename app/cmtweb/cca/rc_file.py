#! /usr/bin/env python
"""
This module provides an object for working with resource files used with the
Component Modeling Tool. Resource files describe how components are connected
with one another, how they are configured, and how they are to be run as
a coupled set of components.
"""


import os
import types
import warnings
from string import Template

from .port import Port


_RC_COMMANDS = set([
    'pulldown',
    'parameters',
    'path',
    'repository',
    'instantiate',
    'connect',
    'go',
    'quit',
])

_RC_HEADER_STR = """
#!ccaffeine bootstrap file.
# ------- don't change anything ABOVE this line.-------------
path set ${project_path}
"""


class ResourceFile(object):
    """
    The ResourceFile object is used to hold a representation of a coupled
    set of components, as with a CMT rc or bld file.

    Create a ResourceFile instance without any commands.

    >>> rc = ResourceFile()

    Add some commands to the rc file either as strings or CmtCommand objects,

    >>> rc.append('instantiate model_name instance_name')
    >>> rc.append(CmtCommand('instantiate another_model another_instance'))
    >>> rc.commands # doctest: +NORMALIZE_WHITESPACE
    [CmtCommand('instantiate model_name instance_name'),
     CmtCommand('instantiate another_model another_instance')]

    >>> rc = ResourceFile([
    ...     CmtCommand('instantiate model_name instance_name'),
    ...     CmtCommand('instantiate another_model another_instance')])
    >>> rc.commands # doctest: +NORMALIZE_WHITESPACE
    [CmtCommand('instantiate model_name instance_name'),
     CmtCommand('instantiate another_model another_instance')]

    Create a ResourceFile instance and fill it with commands from a file.
    The *from_file* keyword is intented to be either an open stream or a
    file name but, in fact, can be either an iterable of commands or a file
    name.

    >>> from StringIO import StringIO
    >>> cmds = StringIO(\"\"\"
    ... instantiate model_name instance_name
    ... instantiate another_model another_instance
    ... \"\"\")
    >>> rc = ResourceFile(cmds)
    >>> rc.commands # doctest: +NORMALIZE_WHITESPACE
    [CmtCommand('instantiate model_name instance_name'),
     CmtCommand('instantiate another_model another_instance')]
    """
    def __init__(self, *args, **kwds):
        from_file = kwds.pop('from_file', None)
        project_path = kwds.pop('project_path', '.')
        validate = kwds.pop('validate', False)
        run = kwds.pop('run', '')

        assert(len(args) in [0, 1])
        if len(args) == 1:
            assert(from_file is None)

        self._validate = validate
        self._commands = []
        self._project_path = os.path.abspath(project_path)
        self._project_classes = set()
        self._project_components = dict()
        self._project_parameters = dict()
        self._connections = []
        try:
            (port, name) = run.split('@')
        except ValueError:
            self._run = None
        else:
            self._run_component(name, port)

        try:
            self.extend(args[0])
        except IndexError:
            if isinstance(from_file, types.StringTypes):
                with open(from_file, 'r') as commands:
                    self.extend(commands)
            elif from_file is not None:
                self.extend(from_file)

        if self._validate:
            try:
                _validate_project_path(self._project_path)
            except ProjectPathError:
                raise

    @property
    def commands(self):
        return self._commands

    def append(self, command):
        """
        Append the resource file *command* to the end of the command stack for
        a resource file. If *command* is not a valid resource file command,
        don't do anything. *command* is either a string whose contents are a
        single command line from either a BLD or RC file, or an instance of
        a CmtCommand.
        """
        if isinstance(command, CmtCommand):
            new_cmd = command
        else:
            if len(command.strip()) > 0:
                new_cmd = CmtCommand(command.strip())
            else:
                return

        (cmd, args) = new_cmd.command, new_cmd.args
        if cmd in _RC_COMMANDS:
            self._commands.append(new_cmd)

            if cmd in ['pulldown', 'instantiate']:
                self._instantiate_component(*args)
            elif cmd == 'parameters':
                (name, key, value) = (args[0], args[1], ' '.join(args[2:]))
                self._set_component_parameter(name, key, value)
            elif cmd == 'path':
                self._project_path = args[1]
            elif cmd in ['go', 'run']:
                self._run_component(args[0], args[1])
            elif cmd in ['connect']:
                self._connect_components(*args)


    def extend(self, commands):
        """
        Extend *commands* to the end of the command stack.
        """
        for command in commands:
            self.append(command)

    def as_string(self):
        """
        Generate a string representation of the current set of RC file
        commands.
        """
        return (os.linesep*2).join([
            self._header(),
            self._repository_block(),
            self._instantiate_block(),
            self._connect_block(),
            self._parameters_block(),
            self._run_block(),
            self._footer(),
        ])

    def __len__(self):
        return len(self._commands)

    def __str__(self):
        return self.as_string()

    def __ne__(self, that):
        return not self == that

    def __cmp__(self, that):
        return set(self.commands) == set(that.commands)

    def __eq__(self, that):
        return set(self.commands) == set(that.commands)

    def __repr__(self):
        commands = []
        for cmd in self.commands:
            commands.append(repr(str(cmd)))
        return 'ResourceFile([%s])' % ', '.join(commands)

    def _connect_components(self, user_name, user_port, provider_name, provider_port):
        self._connections.append(
            (Port(user_port, user_name),
             Port(provider_port, provider_name))
        )

    def _run_component(self, name, port):
        self._run = Port(port, name)

    def _instantiate_component(self, clazz, name):
        if self._validate:
            try:
                _assert_class_in_project(self._project_path, clazz)
            except ProjectPathError:
                raise

        self._project_classes.add(clazz)
        self._project_components[name] = clazz
        self._project_parameters[name] = []

    def _set_component_parameter(self, name, key, value):
        try:
            assert(name in self._project_parameters)
            assert(name in self._project_components)
        except AssertionError:
            warnings.warn("%s: Not instantiate" % name, UserWarning)
        else:
            self._project_parameters[name].append((key, value))

    def _header(self):
        header = Template(_RC_HEADER_STR)
        return os.linesep.join([
            header.substitute(project_path=self._project_path).strip(),
        ])

    def _repository_block(self):
        block = ['# Repositories #']
        for clazz in sorted(set(self._project_components.values())):
            block.append(' '.join(['repository', 'get-global', clazz]))
        return os.linesep.join(block)

    def _instantiate_block(self):
        block = ['# Instantiate components #']
        for (name, clazz) in sorted(self._project_components.items()):
            block.append(' '.join(['instantiate', clazz, name]))
        return os.linesep.join(block)

    def _parameters_block(self):
        block = ['# Parameters #']
        for (name, commands) in self._project_parameters.items():
            if name in sorted(self._project_components):
                block.extend(['', '## %s ##' % name])
                for (key, value) in commands:
                    block.append(' '.join(['parameters', name, key, value]))
            else:
                warnings.warn('%s: Component not instantiated. Skipping parameters section' % name)
        return os.linesep.join(block)

    def _connect_block(self):
        block = ['# Connect components #']
        for (user, provider) in sorted(self._connections):
            block.append(' '.join(['connect',
                                   user.component, user.port,
                                   provider.component, provider.port]))
        return os.linesep.join(block)

    def _run_block(self):
        block = ['# Run #']
        try:
            block.append(' '.join(['go', self._run.component, self._run.port]))
        except (TypeError, AttributeError):
            return ''
        else:
            return os.linesep.join(block)

    def _footer(self):
        block = ['# Quit #']
        block.append('quit')
        return os.linesep.join(block)


class CmtCommand(object):
    """
    The CmtCommand object is used to hold a framework command from an RC or
    BLD file.

    CmtCommand has two construction signatures. The first initializes the
    object with a string that contains a framework command.

    >>> cmd = CmtCommand('go model run')
    >>> cmd.command
    'go'
    >>> cmd.args
    ['model', 'run']
    >>> print cmd
    go model run
    >>> repr(cmd)
    "CmtCommand('go model run')"

    The second uses an iterable with each element being either the command
    or a single argument.

    >>> cmd = CmtCommand(('set', 'path', '/usr/local'))
    >>> cmd.command
    'set'
    >>> cmd.args
    ['path', '/usr/local']
    >>> print cmd
    set path /usr/local
    >>> repr(cmd)
    "CmtCommand('set path /usr/local')"

    For *parameters* commands, the whitespace in the value part of the command
    is preserved.

    >>> cmd = CmtCommand('parameters name Configure var_name 9.99   1.08')
    >>> cmd.command
    'parameters'
    >>> cmd.args
    ['name', 'Configure', 'var_name', '9.99   1.08']

    A command without arguments returns an empty list for is args property.

    >>> cmd = CmtCommand('quit')
    >>> cmd.command
    'quit'
    >>> cmd.args
    []

    """
    def __init__(self, command):
        if isinstance(command, types.StringTypes):
            self.__init__(CmtCommand.split(command))
            #self.__init__(command.split())
        else:
            (self._command, self._args) = (command[0].strip(), [])
            try:
                for word in command[1:]:
                    arg = word.strip()
                    if len(arg) > 0:
                        self._args.append(arg)
            except IndexError:
                pass

    @staticmethod
    def split(command_and_args):
        try:
            (command, args) = command_and_args.split(None, 1)
        except ValueError:
            return [command_and_args.strip()]
        else:
            if command == 'parameters':
                return [command] + args.split(None, 3)
            else:
                return [command] + args.split()
        
    @property
    def command(self):
        """
        The command of the command as a string. This is the first word of the
        command string (or list).
        """
        return self._command

    @property
    def args(self):
        """
        Argument list of the command. These are all the words after the command.
        Returns the arguments as a list of strings, or an empty list if the
        command does not have any arguments.
        """
        return self._args

    def __str__(self):
        return ' '.join([self._command] + self._args)

    def __repr__(self):
        return 'CmtCommand(\'%s\')' % str(self)

    def __eq__(self, that):
        return str(self) == str(that)

    def __ne__(self, that):
        return str(self) != str(that)

    def __cmp__(self, that):
        return str(self) == str(that)

    def __hash__(self):
        return hash(str(self))


class Error(Exception):
    """Base class for errors in this module."""
    pass


class ProjectPathError(Error):
    """
    Raise this error if a project path does not appear to be a valid bocca
    project.
    """
    def __init__(self, msg):
        self._msg = str(msg)

    def __str__(self):
        return self._msg


def bld_to_rc(bld_file, **kwds):
    """
    Convert the contents of *bld_file*, which is either a file-like object or
    a file name of a bld file, into an rc file. The keyword arguments are the
    same as those for the ResourceFile constructor. Return the converted
    file as a string.
    """

    if isinstance(bld_file, types.StringTypes):
        with open(bld_file, 'r') as opened:
            rc_str = bld_to_rc(opened, **kwds)
    else:
        rc_str = str(ResourceFile(bld_file, **kwds))

    return rc_str


def _validate_project_path(project_path, classes=[]):
    import glob

    try:
        assert(os.path.isdir(project_path))
    except AssertionError:
        raise ProjectPathError('Not a directory')

    try:
        assert(len(glob.glob(os.path.join(project_path, '*sidl'))) > 0)
    except AssertionError:
        raise ProjectPathError('No .sidl files')

    try:
        assert(len(glob.glob(os.path.join(project_path, '*cca'))) > 0)
    except AssertionError:
        raise ProjectPathError('No .cca files')

    for clazz in classes:
        try:
            _assert_class_in_project(project_path, clazz)
        except ProjectPathError:
            raise


def _assert_class_in_project(project_path, clazz):
    try:
        assert(os.path.isfile(os.path.join(project_path, clazz + '.cca')))
    except AssertionError:
        raise ProjectPathError('Missing .cca file for %s' % clazz)

    try:
        assert(os.path.isfile(os.path.join(project_path, clazz + '.sidl')))
    except AssertionError:
        raise ProjectPathError('Missing .sidl file for %s' % clazz)


def bld_file_name_to_rc_file_name(bld_file, backup_ext='.bak'):
    """
    Convert *bld_file*, a bld file name, to its corresponding rc file name.
    """
    import shutil

    try:
        bld_name = os.path.realpath(bld_file)
    except AttributeError:
        bld_name = os.path.realpath(bld_file.name)

    (root, _) = os.path.splitext(os.path.basename(bld_name))
    rc_file = root + '.rc'
    if os.path.realpath(rc_file) == os.path.realpath(bld_name):
        shutil.copy2(bld_file, bld_file + backup_ext)
    return root + '.rc'


if __name__ == '__main__':
    import doctest
    doctest.testmod()
