import sqlite3
import requests

from flask import Blueprint
from flask import json, jsonify, url_for
from flask import g, Response, request, abort
from flask import current_app
from flask.ext.login import current_user
from flask.ext.sqlalchemy import SQLAlchemy

from sqlalchemy import Column, Integer, String
from ..database import SESSIONS
from ..utils import as_resource, as_collection
from ..db import tag as tag_db


#Base = SESSIONS['tag']['base']
#db_session = SESSIONS['tag']['session']

tags_page = Blueprint('tags', __name__)


#class Tag(Base):
#    __tablename__ = 'tags'

#    id = Column(Integer, primary_key=True)
#    tag = Column(String(128))
#    owner = Column(String(128))

#    def __init__(self, tag, owner=None):
#        self.tag = tag
#        self.owner = owner or ''

#    def __repr__(self):
#        return '<Tag %r>' % self.tag


#class ModelTag(Base):
#    __tablename__ = 'model_tags'

#    model_id = Column(Integer, primary_key=True)
#    tag_id = Column(Integer)

#    def __init__(self, model, tag):
#        self.model = model
#        self.tag = tag

#    def __repr__(self):
#        return '<ModelTag %r, %r>' % (self.model, self.tag)


def get_all_tags():
    return Tag.query.all()


def add_tag(name, owner=None):
    owner = owner or (current_user.get_id() or "")
    tag = Tag.query.filter_by(tag=name, owner=owner).first()

    if tag is None:
        tag = Tag(name, owner=owner)
        db_session.add(tag)
        db_session.commit()

    return tag


def remove_tag(tag):
    db_session.delete(tag)
    db_session.commit()


def request_user(name):
    resp = requests.get(url_for('users.search', username=name,
                                _external=True))
    users = json.loads(resp.text)
    try:
        return users[0]
    except IndexError:
        return None


def to_resource(tag):
    return {
        '_type': 'tag',
        'id': tag.id,
        'href': url_for('.tag', id=tag.id),
        'tag': tag.tag,
        'owner': tag.owner or None,
        'user': {
            'href': request_user(tag.owner) or None
        },
    }


@tags_page.route('/', methods=['GET', 'POST', 'OPTIONS'])
def show():
    if request.method == 'GET':
        collection = [to_resource(tag) for tag in tag_db.all()]
        return as_collection(collection)
    elif request.method == 'POST':
        data = json.loads(request.data)
        return as_resource(to_resource(tag_db.add(data['tag'])))


@tags_page.route('/search')
def search():
    username = request.args.get('username', None)

    tags = tag_db.query(owner=username)

    #tags = Tag.query.filter_by(owner=username)
    collection = [url_for('.tag', id=tag.id) for tag in tags or []]
    return as_collection(collection)


@tags_page.route('/<int:id>', methods=['GET', 'REMOVE', 'OPTIONS'])
def tag(id):
    tag = tag_db.query(id=id).first() or abort(404)
    #tag = Tag.query.filter_by(id=id).first() or abort(404)

    if request.method == 'REMOVE':
        remove_tag(tag)

    return as_resource(to_resource(tag))


@tags_page.route('/<int:id>/models')
def tags_models(id):
    models = tag_db.models_with_tag(id)

    collection = [url_for('.models', id=m.model_id) for m in models or []]
    return as_collection(collection)
