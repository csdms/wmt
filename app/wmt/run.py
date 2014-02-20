import os
import json
import uuid

from .models import (models, components)
from .config import site


_HOOK_NAMES = set(['pre-stage', 'post-stage'])


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
        raise ValueError(pathname)
        hook = imp.new_module(hook_name)
        def execute(*args):
            pass
        setattr(hook, 'execute', execute)

    return hook


def stagein(id):
    model = json.loads(models.get_model(id).json)['model']

    run_id = str(uuid.uuid4())

    stage_dir = site['stage']
    run_dir = os.path.join(stage_dir, run_id)

    try:
        os.mkdir(run_dir)
        for component in model:
            os.mkdir(os.path.join(run_dir, component['class'].lower()))
    except OSError:
        pass

    for component in model:
        name = component['class'].lower()
        hooks = get_component_hooks(name)

        hooks['pre-stage'].execute()

        files = components.get_component_formatted_input(
            name, **component['parameters'])

        for (filename, contents) in files.items():
            with open(os.path.join(run_dir, name, filename), 'w') as f:
                f.write(contents)

        hooks['post-stage'].execute(name, run_id, component['parameters'])

    return run_id
