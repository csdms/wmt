import web
import json

from ..render import render
from ..models import components as comps
from ..validators import (not_too_long, not_too_short, not_bad_json)
from ..decorators import as_json


class Components(object):
    @as_json
    def GET(self):
        return comps.get_component_names(sort=True)


class Component(object):
    @as_json
    def GET(self, name):
        data = web.input(key=None)

        try:
            comp = comps.get_component(name)
        except comps.IdError:
            raise web.notfound()

        if data.key is not None:
            try:
                return comp[data.key]
            except KeyError:
                raise web.notfound()
        else:
            resp = {}
            for key in ['doi', 'author', 'url', 'summary', 'version', 'id', ]:
                resp[key] = comp[key]
            return resp


class Parameters(object):
    @as_json
    def GET(self, name):
        try:
            comp = comps.get_component(name)
        except comps.IdError:
            raise web.notfound()

        return comp['parameters']


class Parameter(object):
    @as_json
    def GET(self, name, key):
        try:
            comp = comps.get_component(name)
        except comps.IdError:
            raise web.notfound()

        for param in comp['parameters']:
            if param['key'] == key:
                return param

        raise web.notfound()


class Files(object):
    @as_json
    def GET(self, name):
        return comps.get_component_input(name)


class File(object):
    @as_json
    def GET(self, name, file):
        files = comps.get_component_input(name)
        try:
            return files[file]
        except KeyError as error:
            raise web.notfound()


class Outputs(object):
    @as_json
    def GET(self, name):
        try:
            comp = comps.get_component(name)
        except comps.IdError:
            raise web.notfound()

        return comp['provides']


class Inputs(object):
    @as_json
    def GET(self, name):
        try:
            comp = comps.get_component(name)
        except comps.IdError:
            raise web.notfound()

        return comp['uses']
