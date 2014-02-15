import web
import json

from ..render import render
from ..models import components as comps


class List(object):
    def GET(self):
        return json.dumps(comps.get_component_names(sort=True))


class Show(object):
    def GET(self, name):
        user_data = web.input(key=None)
        try:
            comp = comps.get_component(name)
        except comps.IdError:
            raise web.notfound()

        if user_data.key is None:
            return json.dumps(comp)
        else:
            try:
                return json.dumps(comp[user_data.key])
            except KeyError:
                raise web.notfound()


class Dump(object):
    def GET(self):
        return json.dumps(comps.get_components())


class Parameters(object):
    def GET(self, name):
        try:
            return json.dumps(comps.get_component_params(name))
        except KeyError:
            raise web.notfound()


class Input(object):
    def GET(self, name):
        try:
            return render.code(comps.get_component_input(name))
        except KeyError as error:
            return error
            raise web.notfound()


class Format(object):
    def GET(self, name):
        user_data = web.input()
        try:
            return render.code(
                comps.get_component_formatted_input(name, **user_data))
        except KeyError as error:
            return error
            raise web.notfound()


class Defaults(object):
    def GET(self, name):
        try:
            return comps.get_component_defaults(name)
        except KeyError:
            raise web.notfound()

