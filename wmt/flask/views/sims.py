import os

from flask import Blueprint
from flask import json, url_for
from flask import g, request, abort, send_file
from flaskext.uploads import UploadSet

from ..utils import as_resource, as_collection
from ..db import sim as sim_db


sims_page = Blueprint('sims', __name__)
STAGE_DIR = '/data/web/htdocs/wmt/api/dev/files/downloads'


def to_resource(sim):
    #links = []
    #for tag in tag_db.tags_with_model(model.id):
    #    link = dict(rel='collection/tags')
    #    if tag is not None:
    #        link['href'] = url_for('tags.tag', id=tag.id)
    #    else:
    #        link['href'] = None
    #    links.append(link)
    return {
        '_type': 'sim',
        'id': sim.id,
        'uuid': sim.uuid,
        'href': '/api/sims/%d' % sim.id,
        'created': sim.created,
        'updated': sim.updated,
        'owner': sim.owner or None,
        #'links': links,
    }


def to_collection(sims):
    return [to_resource(sim) for sim in sims]


@sims_page.route('/', methods=['GET', 'POST', 'OPTIONS'])
def show():
    if request.method == 'GET':
        sort = request.args.get('sort', 'id')
        order = request.args.get('order', 'asc')

        sims = sim_db.all(sort=sort, order=order)
        collection = [to_resource(sim) for sim in sims]
        return as_collection(collection)
    elif request.method == 'POST':
        data = json.loads(request.data)
        return as_resource(to_resource(
            sim_db.add(data['name'], data['model'])))


@sims_page.route('/<int:id>', methods=['GET', 'PATCH', 'REMOVE'])
def sim(id):
    sim = sim_db.get(id) or abort(404)

    if request.method == 'PATCH':
        data = json.loads(request.data)
        if set(data.keys()).issubset(['status', 'message']):
            sim_db.update_status(id, **data) or abort(401)
        else:
            abort(400)
    elif request.method == 'REMOVE':
        sim_db.remove()

    return as_resource(to_resource(sim))


@sims_page.route('/<int:id>/status', methods=['GET', 'PATCH', 'PUT'])
def status(id):

    if request.method in ['PATCH', 'PUT']:
        data = json.loads(request.data)
        keys = set(data.keys())
        if request.method == 'PATCH' and not keys.issubset(['status',
                                                            'message']):
            abort(400)
        elif request.method == 'PUT' and keys != set(['status', 'message']):
            abort(400)
        sim_db.update_status(**data)

    sim = sim_db.get(id) or abort(404)
    return as_resource({'status': sim.status,
                        'message': sim.message })


@sims_page.route('/<int:id>/files', methods=['GET'])
def files(id):
    import tempfile, tarfile, shutil

    format = request.args.get('format', 'gztar')

    sim = sim_db.get(id) or abort(404)

    try:
        tmpdir = tempfile.mkdtemp(prefix='wmt', suffix='.d')
    except:
        raise
    else:
        archive = os.path.join(tmpdir, str(sim.uuid))
        name = shutil.make_archive(archive, format, STAGE_DIR, sim.uuid)
        return send_file(name, attachment_filename=os.path.basename(name),
                         as_attachment=True)
    finally:
        shutil.rmtree(tmpdir)


@sims_page.route('/<int:id>/actions', methods=['POST'])
def actions(id):
    if request.method == 'POST':
        data = json.loads(request.data)
        if data['action'] == 'start':
            sim_db.start(id)
        elif data['action'] == 'stop':
            sim_db.stop(id)
        else:
            abort(400)
