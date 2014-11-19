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
from ..core import loads_or_fail


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


class JsonResource(object):
    required = set()

    @classmethod
    def from_resource(clazz, req):
        try:
            data = json.loads(req.data)
        except ValueError:
            raise InvalidJsonError()

        missing = clazz.required - set(data.keys())
        if missing:
            raise MissingFieldError(__name__, missing[0])

        return data


class Credentials(JsonResource):
    required = set(['username', 'password'])


class Password(JsonResource):
    required = set(['password'])


class ChangePassword(JsonResource):
    required = set(['old', 'new'])


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
    data = loads_or_fail(request)
    try:
        u, p = data['username'], data['password']
    except KeyError:
        raise InvalidJsonError()

    if users.authenticate(u, p):
        return as_resource(u)
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
            raise MissingFieldError('Credentials', 'username')
        if not password:
            raise MissingFieldError('Credentials', 'password')

    if users.authenticate(username, password):
        login_user(User(username))
        return as_resource(username)

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
    return as_resource("guest")


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

    collection = [u.to_resource() for u in users.all(sort=sort, order=order)]

    return as_collection(collection)


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
    data = Credentials.from_resource(request)

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

    collection = [name.to_resource() for name in names]
    return as_collection(collection)


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
    data = Password.from_resource(request)

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
    u = users.get_or_404(id)
    data = json.loads(request.data)
    data = ChangePassword(request)

    if not users.change_password(u.username, data['old_password'],
                                 data['new_password']):
        raise AuthenticationError()

    return u.jsonify()


@users_page.route('/<int:id>/tags')
def tags(id):
    """Get the tags owned by a user.
    """
    user = users.get_or_404(id)
    collection = [tag.to_resource() for tag in user.tags]
    return as_collection(collection)


@users_page.route('/<int:id>/models')
def models(id):
    """Get the models owned by a user.
    """
    user = users.get_or_404(id)
    collection = [m.to_resource() for m in user.models]
    return as_collection(collection)


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

    collection = [model.to_resource() for model in models]
    return as_collection(collection)


@users_page.route('/whoami')
def whoami():
    """Returns the name of the currently authenticated user.
    """
    return as_resource(current_user.get_id() or "guest")
