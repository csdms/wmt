import requests

from flask import Blueprint
from flask import json, jsonify
from flask import g, request, url_for
from flask.ext.login import current_user
from flask_login import login_user, logout_user

from ..services import users
from ..models.models import Model
from ..utils import as_resource, as_collection
from ..errors import (AuthenticationError, AlreadyExistsError,
                      MissingFieldError, InvalidJsonError)
from ..core import deserialize_request


users_page = Blueprint('users', __name__)


class User(object):
    def __init__(self, id):
        self._id = id

    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    def get_id(self):
        return self._id


@users_page.route('/login', methods=['POST'])
def login_with_post():
    """Authenticate as a user.

    **Example request**:

    .. sourcecode:: http

       POST /users/login HTTP/1.1
       Host: csdms.colorado.edu
       Content-Type: application/json

       {
         "username": "joe_blow",
         "password": "dragon"
       }

    **Example response**:

    .. sourcecode:: http

       HTTP/1.1 200 OK
       Content-Type: application/json

       {
         "_type": "user",
         "id": 123,
         "href": "/users/123",
         "username": "joe_blow",
         "links": [
           {
             "rel": "collection/tags",
             "href": "/users/123/tags"
           },
           {
            "rel": "collection/models",
            "href": "/users/123/models"
           }
         ]
       }

    :reqheader Content-Type: application/json
    :resheader Content-Type: application/json

    :statuscode 200: no error
    :statuscode 400: bad json
    :statuscode 401: authentication error
    """
    data = deserialize_request(request, fields=['username', 'password'])
    try:
        u, p = data['username'], data['password']
    except KeyError:
        raise InvalidJsonError()

    if users.authenticate(u, p):
        return users.first(username=u).jsonify()
    else:
        raise AuthenticationError()


@users_page.route('/login')
def login_with_get():
    """Authenticate as a user.
    """
    auth = request.authorization

    if auth is not None:
        username, password = auth.username, auth.password
    else:
        username = request.args.get('username', None)
        password = request.args.get('password', None)

        if not username:
            raise MissingFieldError('credentials', 'username')
        if not password:
            raise MissingFieldError('credentials', 'password')

    if users.authenticate(username, password):
        login_user(User(username))
        return users.first(username=username).jsonify()

    raise AuthenticationError()


@users_page.route('/logout', methods=['GET'])
def logout():
    """Log out the currently authenticated user.

    **Example request**:

    .. sourcecode:: http

       GET /users/logout HTTP/1.1
       Host: csdms.colorado.edu
    """
    logout_user()
    return "", 204


@users_page.route('/', methods=['GET'])
def show():
    """Get a list of user instances for all users.

    **Example request**:

    .. sourcecode:: http

       GET /users HTTP/1.1
       Host: csdms.colorado.edu
       Content-Type: application/json

    **Example response**:

    .. sourcecode:: http

       HTTP/1.1 200 OK
       Content-Type: application/json

       [{
         "_type": "user",
         "id": 123,
         "href": "/users/123",
         "username": "joe_blow",
         "links": [
           {
             "rel": "collection/tags",
             "href": "/users/123/tags"
           },
           {
            "rel": "collection/models",
            "href": "/users/123/models"
           }
         ]
       }]


    :query sort: name of field by which results are sorted
    :query order: sort result in ascending or descending order (*asc* or
                  *desc*).
    :reqheader Content-Type: application/json
    :resheader Content-Type: application/json
    :statuscode 200: no error
    :statuscode 400: bad json
    :statuscode 401: authentication error
    """
    sort = request.args.get('sort', 'id')
    order = request.args.get('order', 'asc')

    return users.jsonify_collection(users.all(sort=sort, order=order))


@users_page.route('/', methods=['POST'])
def create():
    """Create a new user.

    **Example request**:

    .. sourcecode:: http

       POST /users HTTP/1.1
       Host: csdms.colorado.edu
       Content-Type: application/json

       {
         "username": "joe_blow",
         "password": "dragon"
       }

    **Example response**:

    .. sourcecode:: http

       HTTP/1.1 200 OK
       Content-Type: application/json

       {
         "_type": "user",
         "id": 123,
         "href": "/users/123",
         "username": "joe_blow",
         "links": [
           {
             "rel": "collection/tags",
             "href": "/users/123/tags"
           },
           {
            "rel": "collection/models",
            "href": "/users/123/models"
           }
         ]
       }

    :reqheader Content-Type: application/json
    :resheader Content-Type: application/json
    :statuscode 200: no error
    :statuscode 400: bad JSON
    :statuscode 422: user already exists
    """
    data = deserialize_request(request, fields=['username', 'password'])

    user = users.first(username=data['username'])
    if user:
        raise AlreadyExistsError("User", "username")
    else:
        user = users.create(data['username'], data['password'])
    return user.jsonify()


@users_page.route('/search')
def search():
    """Search for users.
    """
    username = request.args.get('username', None)
    contains = request.args.get('contains', None)

    if contains is not None:
        names = users.contains(contains)
    elif username is not None:
        names = users.find(username=username)
    else:
        names = users.all()

    return as_collection([user.to_resource() for user in names])


@users_page.route('/<int:id>', methods=['GET'])
def user(id):
    """Get a user instance.

    **Example request**:

    .. sourcecode:: http

       GET /users/123 HTTP/1.1
       Host: csdms.colorado.edu
       Content-Type: application/json

    **Example response**:

    .. sourcecode:: http

       HTTP/1.1 200 OK
       Content-Type: application/json

       {
         "_type": "user",
         "id": 123,
         "href": "/users/123",
         "username": "joe_blow",
         "links": [
           {
             "rel": "collection/tags",
             "href": "/users/123/tags"
           },
           {
            "rel": "collection/models",
            "href": "/users/123/models"
           }
         ]
       }

    :reqheader Content-Type: application/json
    :resheader Content-Type: application/json
    :statuscode 200: no error
    :statuscode 404: no user
    """
    return users.get_or_404(id).jsonify()


@users_page.route('/<int:id>', methods=['DELETE'])
def delete(id):
    """Delete a user.

    **Example request**:

    .. sourcecode:: http

       DELETE /users/123 HTTP/1.1
       Host: csdms.colorado.edu
       Content-Type: application/json

       { "password": "dragon" }
    """
    u = users.get_or_404(id)
    data = deserialize_request(request, fields=['password'])

    if users.authenticate(u.username, data['password']):
        users.delete(u)
    else:
        raise AuthenticationError()

    return "", 204


@users_page.route('/<int:id>', methods=['PATCH'])
def change_password(id):
    """Change the password for a user.

    **Example request**:

    .. sourcecode:: http

       PATCH /users/123 HTTP/1.1
       Host: csdms.colorado.edu
       Content-Type: application/json

       {
         "old_password": "dragon",
         "new_password": "mississippi"
       }
    """
    data = deserialize_request(request, require=['password'])

    u = users.get_or_404(id)

    if not users.change_password(u.username, data['password'],
                                 data['password']):
        raise AuthenticationError()

    return u.jsonify()


@users_page.route('/<int:id>/tags')
def tags(id):
    """Get the tags owned by a user.
    """
    user = users.get_or_404(id)
    return users.jsonify_collection(user.tags)


@users_page.route('/<int:id>/models')
def models(id):
    """Get the models owned by a user.
    """
    user = users.get_or_404(id)
    return users.jsonify_collection(user.models)


@users_page.route('/<int:id>/models/search')
def search_models(id):
    """Search models owned by a user.

    **Example request**:

    .. sourcecode:: http

       GET /users/123/models/search HTTP/1.1
       Host: csdms.colorado.edu
       Content-Type: application/json

    :query sort: name of field by which results are sorted (*tag_id* or *tag*)
    :query order: sort result in ascending or descending order (*asc* or
                  *desc*).
    :reqheader Content-Type: application/json
    :resheader Content-Type: application/json
    :statuscode 200: no error
    :statuscode 400: bad json
    :statuscode 401: authentication error
    """
    search = {}
    if 'tag_id' in request.args:
        search['id'] = request.args['tag_id']
    if 'tag' in request.args:
        search['tag'] = request.args['tag']

    models = users.get_or_404(id).models
    if search:
        models = models.filter(Model.tags.any(**search))

    return users.jsonify_collection(models)


@users_page.route('/whoami')
def whoami():
    """Returns the name of the currently authenticated user.
    """
    user = users.first(username=current_user.get_id())
    if user:
        return user.jsonify()
    else:
        return '', 204
