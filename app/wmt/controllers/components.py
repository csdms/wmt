import web
import json

from ..render import render
from ..models.components import (get_component, get_component_names,
                                 get_components, get_component_params,
                                 get_component_input_files,
                                 get_component_defaults, IdError)


class List(object):
    def GET(self):
        return json.dumps(get_component_names(sort=True))


class Show(object):
    def GET(self, name):
        user_data = web.input(key=None)
        try:
            comp = get_component(name)
        except IdError:
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
        return json.dumps(get_components())


class Parameters(object):
    def GET(self, name):
        try:
            return json.dumps(get_component_params(name))
        except KeyError:
            raise web.notfound()


class Input(object):
    def GET(self, name):
        user_data = web.input(defaults='0')
        try:
            return render.code(
                get_component_input_files(
                    name, with_defaults=user_data.defaults=='1'))
        except KeyError:
            raise web.notfound()


class Defaults(object):
    def GET(self, name):
        try:
            #return render.code(get_component_defaults(name))
            #return json.dumps(get_component_defaults(name))
            return get_component_defaults(name)
        except KeyError:
            raise web.notfound()

