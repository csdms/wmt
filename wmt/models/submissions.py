import web
import os
from uuid import uuid4
import json

from . import components, models
from ..config import submission_db as db
from ..config import site
from ..utils.io import write_readme, execute_in_dir
from ..utils.time import current_time_as_string
#from ..utils.ssh import launch_cmt_on_host
from ..session import get_username


class Error(Exception):
    pass


class IdError(Error):
    def __init__(self, id):
        self._id = id

    def __str__(self):
        return '%s: bad id' % self._id


def new(name, model_id):
    now = current_time_as_string(format='iso')
    uuid = str(uuid4())
    data = {
        'name': name,
        'model_id': model_id,
        'uuid': uuid,
        'status': 'submitted',
        'message': 'Run has been submitted',
        'created': now,
        'updated': now,
        'owner': get_username(),
        'stage_dir': os.path.join(site['downloads'], uuid),
    }
    db.insert('submission', **data)

    _create_stage_dir(uuid)

    return data['uuid']


def delete(uuid):
    _remove_stage_dir(uuid)
    id = get_submission(uuid).id

    db.delete('submission', where='uuid=$uuid', vars=locals())
    #db.delete('history', where='submission_id=$id',
    #          vars=dict(submission_id=id))


def _remove_stage_dir(uuid):
    from shutil import rmtree
    rmtree(_get_stage_dir(uuid), ignore_errors=True)


def update(uuid, **kwds):
    kwds['updated'] = current_time_as_string(format='iso')
    db.update('submission', vars=dict(uuid=uuid), where='uuid=$uuid', **kwds)

    #id = get_submission(uuid).id
    #db.insert('history', submission_id=id, updated=kwds['updated'],
    #          message=kwds['message'])


def get_submissions():
    return db.select('submission', order='updated DESC')


def get_uuids():
    entries = db.select('submission', what='uuid')
    return [entry['uuid'] for entry in entries]


def get_submission(uuid):
    try:
        return db.select('submission', where='uuid=$uuid', vars=locals())[0]
    except IndexError:
        raise IdError(uuid)


def get_status(uuid):
    try:
        return db.select('submission', where='uuid=$uuid',
                         vars=locals())[0]
    except IndexError:
        raise IdError(uuid)


def get_model_id(uuid):
    return db.select('submission', what='model_id', where='uuid=$uuid',
                     vars=locals())[0]['model_id']


def get_model(uuid):
    return json.loads(models.get_model(get_model_id(uuid)).json)


def get_components(uuid):
    return get_model(uuid)['model']


def _get_stage_dir(uuid):
    return db.select('submission', what='stage_dir', where='uuid=$uuid',
                      vars=locals())[0]['stage_dir']


def _create_stage_dir(uuid):
    path = _get_stage_dir(uuid)

    try:
        os.mkdir(path)
    except OSError:
        logger.warning('%s: Stage directory already exists' % path)

    write_readme(path, mode='w', params={
        'user': 'anonymous',
        'staged_on': current_time_as_string()
    })


#def launch(uuid, username, host, password=None):
#    script = os.path.join(os.path.dirname(__file__), '..', 'scripts',
#                          'launch.py')
#    resp = launch_cmt_on_host(uuid, host, username, password=password)
#
#    return resp


def get_global_params(params):
    global_params = {
        'run_duration': None,
    }
    for name in global_params:
        try:
            global_params[name] = params[name]
        except Keyerror:
            pass

    return global_params


def get_component_globals(name):
    parameters = components.get_component_params(name)
    return [p['key'] for p in parameters if p.get('global', False)]


def set_global_parameters(components, driver):
    component_params = dict()
    for component in components:
        name = component['id']
        component_params[name] = component['parameters']

    global_params = {}
    for name in get_component_globals(driver):
        global_params[name] = component_params[driver][name]

    for params in component_params.values():
        params.update(global_params)


def set_port_parameters(port, components):
    name = port['name']
    for component in components:
        if component['id'] == name:
            params = component['parameters']
            break
    port['time_step'] = float(params['_update_time_step'])


def stage(uuid):
    import yaml

    os.environ['WMT_INPUT_FILE_PATH'] = os.pathsep.join([
        models.get_model_upload_dir(get_model_id(uuid)),
        os.getcwd(),
    ])

    model, ports = models.get_model_yaml(get_model_id(uuid))
    components = get_components(uuid)

    set_global_parameters(components, model['driver'])

    with execute_in_dir(_get_stage_dir(uuid)) as cwd:
        write_readme('.', mode='a',
                     params={'staged_on': current_time_as_string()})

        update(uuid, status='staging', message='staging components...')
        for component in components:
            stage_component(component, prefix=cwd)

        for port in ports:
            set_port_parameters(port, components)

        with open('model.yaml', 'w') as f:
            f.write(yaml.dump(model, default_flow_style=False))

        with open('components.yaml', 'w') as f:
            f.write(yaml.dump_all(ports, default_flow_style=False))


def _component_stagein(component):
    name = component['class']

    files = components.get_component_formatted_input(
        name, **component['parameters'])

    for (filename, contents) in files.items():
        if not os.path.isfile(filename):
            with open(filename, 'w') as f:
                f.write(contents)

    with open('run.sh', 'w') as f:
        f.write(' '.join(components.get_component_argv(name)))


def _make_stage_dir(dir, ifexists='pass'):
    import errno
    try:
        os.mkdir(dir)
    except OSError as error:
        if error.errno == errno.EEXIST and ifexists == 'pass':
            pass
        else:
            raise error

def prepend_to_path(envvar, path):
    try:
        paths = os.environ[envvar].split(os.pathsep)
    except KeyError:
        paths = []
    paths.insert(0, path)
    os.environ[envvar] = os.pathsep.join(paths)


def stage_component(component, prefix='.'):
    # name = component['class'].lower()
    name = component['class']
    stage_dir = os.path.abspath(os.path.join(prefix, name))

    _make_stage_dir(stage_dir, ifexists='pass')

    prepend_to_path('WMT_INPUT_FILE_PATH',
        os.path.join(site['db'], 'components', name, 'files'))

    hooks = components.get_component_hooks(name)

    with execute_in_dir(stage_dir) as _:
        hooks['pre-stage'].execute(component['parameters'])

    with execute_in_dir(stage_dir) as _:
        _component_stagein(component)

    with execute_in_dir(stage_dir) as _:
        hooks['post-stage'].execute(component['parameters'])
