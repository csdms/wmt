import os

from flask import Blueprint
from flask import json, url_for
from flask import g, request, abort, current_app
from flaskext.uploads import UploadSet, All

from werkzeug import secure_filename

from ..utils import as_resource, as_collection
from ..db import model as model_db
from ..db import tag as tag_db


models_page = Blueprint('models', __name__)

#files = UploadSet('file', All)


UPLOAD_DIR = '/data/web/htdocs/wmt/api/dev/files/uploads'


def components(model):
    return [c['id'] for c in json.loads(model.json)['model']]


def to_resource(model):
    links = [{
        'rel': 'resource/blueprint',
        'href': url_for('.blueprint', id=model.id),
    }]
    for tag in tag_db.tags_with_model(model.id):
        link = dict(rel='collection/tags')
        if tag is not None:
            link['href'] = url_for('tags.tag', id=tag.id)
        else:
            link['href'] = None
        links.append(link)
    for name in components(model):
        links.append({
            'rel': 'resource/component',
            'href': url_for('components.component', name=name),
        })
    return {
        '_type': 'model',
        'id': model.id,
        'href': '/api/models/%d' % model.id,
        'date': model.date,
        'owner': model.owner or None,
        'name': model.name,
        'links': links,
    }


def to_collection(models):
    return [to_resource(model) for model in get_all_models()]


@models_page.route('/', methods=['GET', 'POST', 'OPTIONS'])
def show():
    if request.method == 'GET':
        collection = [to_resource(model) for model in model_db.all()]
        return as_collection(collection)
    elif request.method == 'POST':
        data = json.loads(request.data)
        return as_resource(to_resource(
            model_db.add(data['name'], data['json'])))


@models_page.route('/<int:id>', methods=['GET', 'REMOVE', 'OPTIONS', 'PATCH'])
def model(id):
    model = model_db.query(id=id).first() or abort(404)

    if request.method == 'REMOVE':
        model_db.remove(model)
    elif request.method == 'PATCH':
        data = json.loads(request.data)
        model_db.update(id, **data)

    return as_resource(to_resource(model))


@models_page.route('/<int:id>/blueprint', methods=['GET'])
def blueprint(id):
    model = model_db.query(id=id).first() or abort(404)

    return as_resource(json.loads(model.json))


@models_page.route('/<int:id>/uploads', methods=['GET', 'POST', 'PUT', 'REMOVE'])
def uploads(id):
    model = model_db.query(id=id).first() or abort(404)
    model_uploads = os.path.join(UPLOAD_DIR, str(id))

    if request.method in ['PUT', 'POST']:
        file = request.files['file']
        filename = secure_filename(file.filename)
        path = os.path.join(model_uploads, filename)
        if os.path.isfile(path):
            if request.method == 'POST':
                abort(400)
            else:
                os.remove(path)
        file.save(path)
    elif request.method == 'REMOVE':
        data = json.loads(request.data)
        filename = secure_filename(data['filename'])
        path = os.path.join(model_uploads, filename)
        if os.path.isfile(path):
            os.remove(path)

    return json.dumps(os.listdir(model_uploads))


@models_page.route('/search')
def search():
    username = request.args.get('username', None)

    models = model_db.query(owner=username)
    #models = Model.query.filter_by(owner=username)
    collection = [url_for('.model', id=m.id) for m in models or []]
    return as_collection(collection)


@models_page.route('/<int:id>/tags')
def models_tags(id):
    tags = tag_db.tags_with_model(id)
    collection = [url_for('tags.tag', id=t.tag_id) for t in tags or []]
    return as_collection(collection)
