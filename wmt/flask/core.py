import sqlite3
import json

from flask import jsonify, abort, Response
from flask_sqlalchemy import SQLAlchemy
from flask_sqlalchemy import sqlalchemy
from .errors import InvalidJsonError, MissingFieldError, WrongJsonError


db = SQLAlchemy()


def deserialize_request(request, fields=None, require='all'):
    assert(require in ['all', 'none', 'some'])
    if fields:
        fields = set(fields)
    else:
        fields = set()

    try:
        data = json.loads(request.data)
    except ValueError:
        raise InvalidJsonError()

    try:
        provided_fields = set(data.keys())
    except AttributeError:
        raise WrongJsonError('resource')

    if not provided_fields.issubset(fields):
        raise WrongJsonError('resource')

    if require == 'all' and provided_fields != fields:
        raise MissingFieldError('resource', fields - provided_fields)
    elif require == 'some' and len(provided_fields) == 0:
        raise MissingFieldError('resource', fields)

    return data


class JsonMixin(object):
    __public_fields__ = set()
    __hidden_fields__ = set()

    def fields(self):
        import inspect
        members = inspect.getmembers(self,
                                     lambda p: not inspect.isroutine(p))    
        fields = []
        for member in members:
            if not member[0].startswith('_'):
                fields.append(member[0])
        return set(fields)

    def to_resource(self):
        public = self.__public_fields__ or self.fields()
        hidden = self.__hidden_fields__ or set()

        resource = {'@type': self.__class__.__name__.lower()}

        for field in public - hidden:
            value = getattr(self, field)
            if isinstance(value, property):
                resource[field] = value.fget(self)
            else:
                resource[field] = value

        try:
            resource['links'] = self.object_links
        except AttributeError:
            pass

        try:
            link_objects = self.link_objects
        except AttributeError:
            pass
        else:
            for obj, href in link_objects.items():
                resource[obj] = href

        return resource

    def to_json(self):
        return json.dumps(self.to_resource(), sort_keys=True, indent=2,
                          separators=(',', ': '))

    def jsonify(self, **kwds):
        return Response(self.to_json(),
                        mimetype='application/x-resource+json; charset=utf-8')


class Service(object):
    __model__ = None

    def _is_instance_or_raise(self, model):
        if not isinstance(model, self.__model__):
            raise ValueError('%r is not of type %r' % (model, self.__model__))
        return True


    def _add_or_retry(self, model, max_tries=10):
        tries = 0
        while tries < max_tries:
            try:
                db.session.add(model)
                db.session.commit()
            except Exception as error:
                db.session.rollback()
                tries += 1
            else:
                break

        return tries < max_tries

    def save(self, model):
        self._is_instance_or_raise(model)

        if not self._add_or_retry(model, max_tries=10):
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
        self._is_instance_or_raise(model)
        for key, value in kwds.items():
            getattr(model, key).append(value)
        db.session.commit()

    def delete(self, model):
        self._is_instance_or_raise(model)
        db.session.delete(model)
        db.session.commit()

    @staticmethod
    def jsonify_collection(models):
        return Response(
            json.dumps([model.to_resource() for model in models],
                       sort_keys=True, indent=2,
                       separators=(',', ': ')),
            mimetype='application/x-collection+json; charset=utf-8')
