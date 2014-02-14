import os
import json

from ..config import palette, site


class Error(Exception):
    pass


class IdError(Error):
    def __init__(self, id):
        self._id = id

    def __str__(self):
        return str(self._id)


def get_components():
    return palette.values()


def get_component_names(sort=False):
    names = list(palette.keys())
    if sort:
        names.sort()
    return names


def get_component(name):
    try:
        return palette[name]
    except KeyError:
        raise IdError(name)


def get_component_params(name):
    comp = get_component(name)
    return comp['parameters']


def get_component_defaults(name):
    defaults = {}

    comp = get_component(name)
    for parameter in comp['parameters']:
        defaults[parameter['key']] = parameter['value']['default']

    #return '{january_temperature_mean}'.format(**defaults)
    #return str(type(defaults['january_temperature_mean']))
    return defaults


def get_component_input_files(name, with_defaults=False):
    import string

    input_file_dir = os.path.join(site['data'], 'components')

    comp = get_component(name)

    if 'input_files' not in comp:
        return ''

    mapping = get_component_defaults(name)

    lines = []
    for filename in comp['input_files']:
        path_to_file = os.path.join(input_file_dir, filename)
        lines.append('file: %s' % filename)
        lines.append('-------------------')
        with open(path_to_file, 'r') as file:
            if with_defaults:
                #contents = string.Template(file.read())
                contents = file.read()
                lines.append(contents.format(**mapping))
                #lines.append(contents.substitute(mapping))
            else:
                lines.append(file.read())
        lines.append('-------------------')

    return os.linesep.join(lines)

