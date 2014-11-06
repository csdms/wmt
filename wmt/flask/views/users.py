import requests

from flask import Blueprint
from flask import json
from flask import g, request, abort, url_for
from flask.ext.login import current_user
from flask_login import login_user, logout_user

from ..database import SESSIONS
from ..utils import as_resource, as_collection
from ..db import users as user_db


users_page = Blueprint('users', __name__)


class User(object):
    def __init__(self, id):
        self._id = str(id)

    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    def get_id(self):
        return self._id


@users_page.route('/login', methods=['POST', 'GET'])
def login():
    if request.method == 'POST':
        data = json.loads(request.data)
        if user_db.authenticate_user(data['username'], data['password']):
            return as_resource(data['username'])
    else:
        auth = request.authorization

        if auth is not None:
            username, password = auth.username, auth.password
        else:
            username = request.args.get('username', None)
            password = request.args.get('password', None)

        if user_db.authenticate_user(username, password):
            login_user(User(username))
            return as_resource(username)

        return abort(401)


@users_page.route('/logout', methods=['GET'])
def logout():
    logout_user()
    return as_resource("guest")


def to_resource(user):
    return {
        '_type': 'user',
        'id': user.id,
        'href': url_for('.user', id=user.id),
        'username': user.username,
        'links': [
            {'rel': 'collection/tags',
             'href': url_for('.tags', id=user.id)
            },
            {'rel': 'collection/models',
             'href': url_for('.models', id=user.id)
            },
        ]
    }


def request_user_tags(id):
    user = user_db.get(id) or abort(404)
    tags = requests.get(url_for('tags.search',
                                username=user.username,
                                _external=True)).text
    return json.loads(tags)


def request_user_models(id):
    user = user_db.get(id) or abort(404)
    models = requests.get(url_for('models.search',
                                  username=user.username,
                                  _external=True)).text
    return json.loads(models)


@users_page.route('/', methods=['GET', 'POST'])
def show():
    if request.method == 'GET':
        sort = request.args.get('sort', 'id')
        order = request.args.get('order', 'asc')

        users = user_db.all(sort=sort, order=order)
        collection = [to_resource(u) for u in users]

        return as_collection(collection)
    elif request.method == 'POST':
        data = json.loads(request.data)
        return as_resource(to_resource(
            add_user(data['user'], data['password'])))


#@users_page.route('/search')
#def search():
#    username = request.args.get('username', None)

#    user = user_db.get_by_name(username)

#    if user:
#        collection = [url_for('.user', id=user.id)]
#    else:
#        collection = []
#    #collection = [url_for('.user', id=u.id) for u in users or []]
#    return as_collection(collection)


@users_page.route('/search')
def search():
    username = request.args.get('username', None)
    contains = request.args.get('contains', None)

    if contains is not None:
        names = user_db.contains(contains)
    elif username is not None:
        names = user_db.get_by_name(username)
    else:
        names = user_db.all()

    collection = [to_resource(name) for name in names]
    return as_collection(collection)


@users_page.route('/<int:id>', methods=['GET', 'REMOVE', 'PATCH',
                                        'OPTIONS'])
def user(id):
    u = user_db.get(id) or abort(404)

    if request.method == 'REMOVE':
        abort(405)
    elif request.method == 'PATCH':
        data = json.loads(request.data)
        if not user_db.change_password(u.username, data['old'],
                                       data['new']):
            abort(401)

    return as_resource(to_resource(u))


@users_page.route('/<int:id>/tags')
def tags(id):
    return as_collection(request_user_tags(id))


@users_page.route('/<int:id>/models')
def models(id):
    return as_collection(request_user_models(id))


@users_page.route('/whoami')
def whoami():
    return as_resource(current_user.get_id() or "guest")
