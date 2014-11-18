import os

from flask import Blueprint
from flask import json, url_for
from flask import g, request, abort, current_app
from flask.ext.login import current_user, login_required

from werkzeug import secure_filename

from ..utils import as_resource, as_collection, jsonify_collection
from ..services import models, tags


models_page = Blueprint('models', __name__)


def components(model):
    return [c['id'] for c in json.loads(model.json)['model']]


def to_resource(model):
    links = [{
        'rel': 'resource/blueprint',
        'href': url_for('.blueprint', id=model.id),
    }]
    #for tag in tag_db.tags_with_model(model.id):
    for tag in model.tags:
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


@models_page.route('/', methods=['GET'])
def show():
    return jsonify_collection(models.all())


@models_page.route('/', methods=['POST'])
@login_required
def add():
    data = json.loads(request.data)
    owner = current_user.get_id()
    #return as_resource(to_resource(
    #    models.create(data['name'], data['json'], owner=owner)))
    return models.create(data['name'], data['json'], owner=owner).jsonify()


@models_page.route('/<int:id>', methods=['GET'])
def model(id):
    return models.get_or_404(id).jsonify()


@models_page.route('/<int:id>', methods=['PATCH'])
@login_required
def edit(id):
    model = models.get_or_404(id)
    data = json.loads(request.data)
    models.update(model, **data)
    return model.jsonify()


@models_page.route('/<int:id>', methods=['DELETE'])
@login_required
def delete(id):
    model = models.get_or_404(id)
    models.delete(model)
    return model.jsonify()


@models_page.route('/<int:id>/blueprint', methods=['GET'])
def blueprint(id):
    model = models.get_or_404(id)
    return as_resource(json.loads(model.json))


@models_page.route('/<int:id>/uploads', methods=['GET'])
def fetch_uploads(id):
    model = models.get_or_404(id)
    model_uploads = os.path.join(current_app.config['UPLOAD_DIR'], str(id))
    return json.dumps(os.listdir(model_uploads))


@models_page.route('/<int:id>/uploads', methods=['POST', 'PUT', 'DELETE'])
@login_required
def uploads(id):
    model = models.get_or_404(id)
    model_uploads = os.path.join(current_app.config['UPLOAD_DIR'], str(id))

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
    elif request.method == 'DELETE':
        data = json.loads(request.data)
        filename = secure_filename(data['filename'])
        path = os.path.join(model_uploads, filename)
        if os.path.isfile(path):
            os.remove(path)

    return json.dumps(os.listdir(model_uploads))


@models_page.route('/search')
def search():
    username = request.args.get('username', None)
    return jsonify_collection(models.find(owner=username))


@models_page.route('/<int:id>/tags', methods=['GET'])
def get_tags(id):
    model = models.get_or_404(id)
    return jsonify_collection(model.tags)


@models_page.route('/<int:id>/tags', methods=['POST'])
@login_required
def add_a_tag(id):
    model = models.get_or_404(id)

    data = json.loads(request.data)
    tag = tags.get(data['id']) or abort(400)
    models.append(model, tags=tag)
    return jsonify_collection(model.tags)
