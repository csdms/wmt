import sqlite3
from datetime import datetime

from flask import Blueprint
from flask import json, jsonify
from flask import g, Response, request, abort
from flask import current_app
from flask.ext.login import current_user
from flask.ext.sqlalchemy import SQLAlchemy

from sqlalchemy import Column, Integer, Text
from ..database import SESSIONS
from ..utils import as_resource, as_collection


Base = SESSIONS['wmt']['base']
db_session = SESSIONS['wmt']['session']

models_page = Blueprint('wmt', __name__)


class Model(Base):
    __tablename__ = 'models'

    id = Column(Integer, primary_key=True)
    name = Column(Text)
    date = Column(Text)
    json = Column(Text)
    owner = Column(Text)

    def __init__(self, name, json, owner=None):
        self.name = name
        self.json = json
        self.date = datetime.now().isoformat()
        self.owner = owner or ""

    def __repr__(self):
        return '<Model %r>' % self.name


def to_resource(model):
    return {
        '_type': 'model',
        'id': model.id,
        'href': '/api/models/%d' % model.id,
        'date': model.date,
        'owner': model.owner,
    }


def to_collection(models):
    return [to_resource(model) for model in get_all_models()]


def get_all_models():
    return Model.query.all()


def add_model(name, json):
    owner = current_user.get_id() or ""
    model = Model.query.filter_by(name=name, owner=owner).first()

    if model is None:
        model = Model(name, json, owner=owner)
        db_session.add(model)
        db_session.commit()

    return model


def remove_model(model):
    db_session.delete(model)
    db_session.commit()


@models_page.route('/', methods=['GET', 'POST', 'OPTIONS'])
def show():
    if request.method == 'GET':
        collection = [to_resource(model) for model in get_all_models()]
        return as_collection(collection)
    elif request.method == 'POST':
        data = json.loads(request.data)
        return as_resource(to_resource(
            add_model(data['name'], data['json'])))


@models_page.route('/<int:id>', methods=['GET', 'REMOVE', 'OPTIONS'])
def model(id):
    model = Model.query.filter_by(id=id).first() or abort(404)

    if request.method == 'REMOVE':
        remove_tag(model)

    return as_resource(to_resource(model))
