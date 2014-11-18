from flask import Blueprint
from flask import json, jsonify, url_for
from flask import g, Response, request, abort
from flask import current_app
from flask.ext.login import current_user, login_required

from ..utils import as_resource, as_collection
from ..services import tags, users
from ..errors import (AuthenticationError, AuthorizationError,
                      AlreadyExistsError)


tags_page = Blueprint('tags', __name__)


@tags_page.route('/', methods=['GET', 'OPTIONS'])
def show():
    collection = [tag.to_resource() for tag in tags.all()]
    return as_collection(collection)


@tags_page.route('/', methods=['POST'])
@login_required
def add():
    data = json.loads(request.data)
    owner = users.first(username=current_user.get_id())
    if tags.first(tag=data['tag'], owner=owner.id):
        raise AlreadyExistsError("Tag", "%s:%s" % (data['tag'], owner))
    return tags.create(data['tag'], owner=owner.id).jsonify()


@tags_page.route('/search')
def search():
    username = request.args.get('username', None)
    owner = users.first(username=username)

    tags_list = tags.find(owner=owner)

    collection = [url_for('.tag', id=tag.id) for tag in tags_list]
    return as_collection(collection)


@tags_page.route('/<int:id>', methods=['GET'])
def tag(id):
    return tags.get_or_404(id=id).jsonify()


@tags_page.route('/<int:id>', methods=['DELETE'])
@login_required
def remove(id):
    tag = tags.get_or_404(id)
    user = users.first(username=current_user.get_id())
    if user.id != tag.owner:
        raise AuthorizationError()
    tags.delete(tag)
    return tag.jsonify()


@tags_page.route('/<int:id>/models')
def tags_models(id):
    models = tags.models_with_tag(id) or []

    collection = [url_for('.models', id=m.model_id) for m in models]
    return as_collection(collection)
