import web
import os
from uuid import uuid4
import json

from . import components, models
from ..config import submission_db as db
from ..config import site
from ..utils.io import write_readme, execute_in_dir
from ..utils.time import current_time_as_string
from ..utils.ssh import launch_cmt_on_host


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
        'owner': web.ctx.session.username,
        'stage_dir': os.path.join(site['downloads'], uuid),
    }
    db.insert('submission', **data)

    _create_stage_dir(uuid)

    return data['uuid']


def delete(uuid):
    _remove_stage_dir(uuid)
    db.delete('submission', where='uuid=$uuid', vars=locals())


def _remove_stage_dir(uuid):
    from shutil import rmtree
    rmtree(_get_stage_dir(uuid), ignore_errors=True)


def update(uuid, **kwds):
    kwds['updated'] = current_time_as_string(format='iso')
    db.update('submission', vars=dict(uuid=uuid), where='uuid=$uuid', **kwds)


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


def launch(uuid, username, host, password=None):
    script = os.path.join(os.path.dirname(__file__), '..', 'scripts',
                          'launch.py')
    resp = launch_cmt_on_host(uuid, host, username, password=password)

    return resp


def stage(uuid):
    path = _get_stage_dir(uuid)
    write_readme(path, mode='a', params={
        'staged_on': current_time_as_string()
    })

    os.environ['WMT_INPUT_FILE_PATH'] = os.pathsep.join([
        models.get_model_upload_dir(get_model_id(uuid)),
        os.getcwd(),
    ])

    update(uuid, status='staging', message='staging components...')
    for component in get_components(uuid):
        update(uuid,
            status='staging', message='staging %s...' % component['class'])
        stage_component(path, component)


def _component_stagein(component):
    name = component['class'].lower()

    files = components.get_component_formatted_input(
        name, **component['parameters'])

    for (filename, contents) in files.items():
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


def stage_component(prefix, component):
    name = component['class'].lower()
    stage_dir = os.path.join(prefix, name)

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
