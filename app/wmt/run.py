from __future__ import print_function

import os
import json
import uuid
import shutil
import datetime

from .models import (models, components)
from .config import (site, logger)


_HOOK_NAMES = set(['pre-stage', 'post-stage'])


class execute_in_dir(object):
    def __init__(self, dir):
        self._init_dir = os.getcwd()
        self._exe_dir = dir

    def __enter__(self):
        os.chdir(self._exe_dir)
        return os.getcwd()

    def __exit__(self, type, value, traceback):
        os.chdir(self._init_dir)
        return isinstance(value, OSError)


def path_to_hook(name, hook):
    return os.path.join(site['data'], 'components', name, 'hooks',
                        hook + '.py')


def get_component_hooks(name):
    hooks = {}
    for hook in _HOOK_NAMES:
        hooks[hook] = get_component_hook(name, hook)
    return hooks


def get_component_hook(name, hook_name):
    import imp

    pathname = path_to_hook(name, hook_name)
    try:
        hook = imp.load_source(hook_name, pathname)
    except (IOError, ImportError):
        hook = imp.new_module(hook_name)
        def execute(*args):
            pass
        setattr(hook, 'execute', execute)

    return hook


def _component_stagein(component):
    files = components.get_component_formatted_input(
        component['class'].lower(), **component['parameters'])

    for (filename, contents) in files.items():
        with open(filename, 'w') as f:
            f.write(contents)


def current_time_as_string():
    from datetime import datetime
    return datetime.now().isoformat(' ')


def write_to_readme(path, mode, **kwds):
    with open(os.path.join(path, 'README'), mode) as readme:
        for item in kwds.items():
            print('%s: %s' % item, file=readme, end=os.linesep)


def stage_component(prefix, component):
    name = component['class'].lower()
    stage_dir = os.path.join(prefix, name)

    os.mkdir(stage_dir)

    hooks = get_component_hooks(name)

    with execute_in_dir(stage_dir) as _:
        hooks['pre-stage'].execute(component['parameters'])

    with execute_in_dir(stage_dir) as _:
        _component_stagein(component)

    with execute_in_dir(stage_dir) as _:
        hooks['post-stage'].execute(component['parameters'])


def stagein(id):
    model = json.loads(models.get_model(id).json)['model']

    run_id = str(uuid.uuid4())

    run_dir = os.path.join(site['stage'], run_id)

    os.mkdir(run_dir)
    write_to_readme(run_dir, 'w', user='nobody', start=current_time_as_string())

    for component in model:
        stage_component(run_dir, component)

    return run_id


def run(run_id, id):
    pass


def stageout(run_id):
    run_dir = os.path.join(site['stage'], run_id)
    dropoff_dir = os.path.join('/data/ftp/pub/users/wmt', run_id)

    write_to_readme(run_dir, 'a', stop=current_time_as_string())
    shutil.move(run_dir, dropoff_dir)

    return dropoff_dir
