import os

from flask import Blueprint
from flask import json, url_for, current_app
from flask import g, request, abort, send_file
from flask.ext.login import current_user, login_required

from ..utils import as_resource, as_collection
from ..errors import InvalidFieldError, AuthorizationError
from ..services import sims, users
from ..core import deserialize_request
from ..tasks import exec_remote_wmt


sims_page = Blueprint('sims', __name__)


def assert_owner_or_raise(sim):
    user = users.first(username=current_user.get_id())
    if user.id != sim.owner:
        raise AuthorizationError()


@sims_page.route('/')
def show():
    sort = request.args.get('sort', 'id')
    order = request.args.get('order', 'asc')

    return sims.jsonify_collection(sims.all(sort=sort, order=order))


@sims_page.route('/', methods=['POST'])
@login_required
def new():
    data = deserialize_request(request, fields=['name', 'model'])
    user = users.first(username=current_user.get_id())
    sim = sims.create(data['name'], data['model'], owner=user.id)
    sim.create_stage_dir()
    return sim.jsonify()


@sims_page.route('/<int:id>')
def sim(id):
    return sims.get_or_404(id).jsonify()


@sims_page.route('/<int:id>', methods=['PATCH', 'PUT'])
@login_required
def update(id):
    sim = sims.get_or_404(id)

    assert_owner_or_raise(sim)

    kwds = dict(fields=['status', 'message'])
    if request.method == 'PATCH':
        kwds['require'] = 'some'
    data = deserialize_request(request, **kwds)

    sims.update_status(id, **data) or abort(401)

    return sim.jsonify()


@sims_page.route('/<int:id>', methods=['DELETE'])
@login_required
def delete(id):
    sim = sims.get_or_404(id)
    user = users.first(username=current_user.get_id())
    if user.id != sim.owner:
        raise AuthorizationError()
    sims.delete(sim)
    return "", 204


@sims_page.route('/<int:id>/files')
def files(id):
    import tempfile, tarfile, shutil

    format = request.args.get('format', 'gztar')

    sim = sims.get_or_404(id)

    try:
        tmpdir = tempfile.mkdtemp(prefix='wmt', suffix='.d')
    except:
        raise
    else:
        archive = os.path.join(tmpdir, str(sim.uuid))
        name = shutil.make_archive(archive, format,
                                   current_app.config['STAGE_DIR'], sim.uuid)
        return send_file(name, attachment_filename=os.path.basename(name),
                         as_attachment=True)
    finally:
        shutil.rmtree(tmpdir)


@sims_page.route('/<int:id>/start', methods=['POST'])
def start(id):
    sim = sims.get_or_404(id)

    data = deserialize_request(request, fields=['host',
                                                'username',
                                                'password'])

    hosts = current_app.config['WMT_EXEC_HOSTS']
    if data['host'] not in hosts:
        raise InvalidFieldError('start', 'host')
    else:
        host_config = hosts[data['host']]

    return exec_remote_wmt(data['host'], sim.uuid,
                    username=data['username'],
                    password=data['password'],
                    which_wmt_exe=host_config['which_wmt_exe'])


@sims_page.route('/<int:id>/stop', methods=['POST'])
def stop(id):
    sim = sims.get_or_404(id)

    stop_simulation(sim.uuid)

    return '', 204
