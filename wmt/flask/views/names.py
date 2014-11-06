import sqlite3
from flask import Blueprint
from flask import json, jsonify
from flask import g, Response, request, abort
from flask import current_app

from sqlalchemy import Column, Integer, String
from ..database import SESSIONS
from ..utils import as_resource, as_collection
from ..db import names as name_db

from standard_names import StandardName


Base = SESSIONS['names']['base']
db_session = SESSIONS['names']['session']

names_page = Blueprint('names', __name__)


def json_response(resp):
    return Response(json.dumps(resp, sort_keys=True, indent=2,
                               separators=(',', ': ')),
                   mimetype='application/json; charset=utf-8')


def to_resource(name, brief=False):
    if brief:
        return {'id': name.id, 'name': name.name}
    else:
        sn = StandardName(name.name)
        return {
            'id': name.id,
            'href': '/api/names/%d' % name.id,
            'name': name.name,
            'object': sn.object,
            'quantity': sn.quantity,
            'operators': sn.operators,
        }


@names_page.route('/')
def show():
    sort = request.args.get('sort', 'id')
    order = request.args.get('order', 'asc')

    names = name_db.all(sort=sort, order=order)
    collection = [to_resource(name, brief=True) for name in names]
    return json_response(collection)


@names_page.route('/<int:id>')
def name(id):
    name = name_db.get_by_id(id) or abort(404)
    return json_response(to_resource(name))


@names_page.route('/search')
def search():
    contains = request.args.get('contains', None)
    if contains is not None:
        names = name_db.contains(contains)
    else:
        names = name_db.all()
    collection = [to_resource(name, brief=True) for name in names]
    return json_response(collection)
