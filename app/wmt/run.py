import os
import json
import uuid

from .models import (models, components)
from .config import site


def get_component_hooks(name):
    import imp

    try:
        (fp, pathname, description) = imp.find_module(
            'hooks', [os.path.join(site['data'], 'components')])
    except ImportError:
        raise ValueError(os.path.join(site['data'], 'components'))

    try:
        hooks = imp.load_module('hooks', fp, pathname, description)
    except ImportError:
        hooks = None
    finally:
        if fp:
            fp.close()

    return hooks


def stagein(id):
    model = json.loads(models.get_model(id).json)

    run_id = str(uuid.uuid4())

    stage_dir = site['stage']
    run_dir = os.path.join(stage_dir, run_id)

    try:
        os.mkdir(run_dir)
    except OSError:
        pass

    for component in model['model']:
        name = component['class'].lower()
        os.mkdir(os.path.join(run_dir, name))
        try:
            input_files = components.get_component(name)['input_files']
        except KeyError:
            pass
        else:
            hooks = get_component_hooks(name)
            hooks.stagein_pre()

            files = components.get_component_formatted_input(name, **component['parameters'])
            for (filename, contents) in files.items():
                with open(os.path.join(run_dir, name, filename), 'w') as f:
                    f.write(contents)

            hooks.stagein_post(name, run_id, component['parameters'])


    return run_id
