from flask import Blueprint
from flask import json, jsonify, url_for
from flask import g, Response, request, abort
from flask import current_app
from flask.ext.login import current_user, login_required

from ..utils import as_resource, as_collection
from ..services import tags, users
from ..errors import (AuthenticationError, AuthorizationError,
                      AlreadyExistsError)
from ..core import deserialize_request


tags_page = Blueprint('tags', __name__)


@tags_page.route('/', methods=['GET', 'OPTIONS'])
def show():
    return tags.jsonify_collection(tags.all())


@tags_page.route('/', methods=['POST'])
@login_required
def add():
    data = deserialize_request(request, fields=['tag'])
    owner = users.first(username=current_user.get_id())
    if tags.first(tag=data['tag'], owner=owner.id):
        raise AlreadyExistsError("tag", "%s:%s" % (data['tag'], owner))
    return tags.create(data['tag'], owner=owner.id).jsonify()


@tags_page.route('/search')
def search():
    username = request.args.get('username', None)
    owner = users.first(username=username)

    if owner:
        return tags.jsonify_collection(tags.find(owner=owner.id))
    else:
        return tags.jsonify_collection([])


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
    return '', 204


@tags_page.route('/<int:id>/models')
def tags_models(id):
    from ..models.models import Model
    models = tags.get_or_404(id).models
    return tags.jsonify_collection(models.filter(Model.tags.any(id=id)))
