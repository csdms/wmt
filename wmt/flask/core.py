import sqlite3
import json

from flask import jsonify, Response
from flask_sqlalchemy import SQLAlchemy
from .errors import InvalidJsonError


db = SQLAlchemy()


def loads_or_fail(resp):
    try:
        return json.loads(resp.data)
    except :
        raise InvalidJsonError()


class JsonMixin(object):
    def jsonify(self):
        return Response(json.dumps(self.to_resource(),
                                   sort_keys=True, indent=2,
                                   separators=(',', ': ')),
                        mimetype='application/x-resource+json; charset=utf-8')


class Service(object):
    __model__ = None

    def _is_instance_or_raise(self, model):
        if not isinstance(model, self.__model__):
            raise ValueError('%r is not of type %r' % (model, self.__model__))
        return True

    def save(self, model):
        self._is_instance_or_raise(model)

        db.session.add(model)
        tries, max_tries = 0, 10
        while tries < max_tries:
            try:
                db.session.commit()
            except sqlite3.OperationalError:
                tries += 1
            else:
                break

        if tries == max_tries:
            abort(503)

        return model

    def all(self, sort=None, order=None):
        if sort is None:
            entries = self.__model__.query.all()
        else:
            entries = self.__model__.query.order_by(
                getattr(self.__model__, sort))

        if order == 'asc':
            return entries
        else:
            return entries[::-1]

    def get(self, id):
        return self.__model__.query.get(id)

    def get_some(self, *ids):
        return self.__model__.query.filter(self.__model__.id.in_(ids)).all()

    def find(self, **kwds):
        return self.__model__.query.filter_by(**kwds)

    def first(self, **kwds):
        return self.find(**kwds).first()

    def get_or_404(self, id):
        return self.__model__.query.get_or_404(id)

    def new(self, *args, **kwds):
        return self.__model__(*args, **kwds)

    def create(self, *args, **kwds):
        return self.save(self.new(*args, **kwds))

    def update(self, model, **kwds):
        self._is_instance_or_raise(model)
        for (key, value) in kwds.items():
            setattr(model, key, value)
        self.save(model)
        return model

    def append(self, model, **kwds):
        for key, value in kwds.items():
            getattr(model, key).append(value)
        db.session.commit()

    def delete(self, model):
        self._is_instance_or_raise(model)
        db.session.delete(model)
        db.session.commit()
