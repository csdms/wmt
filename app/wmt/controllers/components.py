import web
import json

from ..models.components import (get_component, get_component_names,
                                 get_components, IdError)


class List(object):
    def GET(self):
        return json.dumps(get_component_names(sort=True))


class Show(object):
    def GET(self, name):
        try:
            return json.dumps(get_component(name))
        except IdError:
            raise web.notfound()


class Dump(object):
    def GET(self):
        return json.dumps(get_components())


class Fill(object):
    def GET(self, name):
        try:
            return json.dumps(PALETTE[name])
        except KeyError:
            raise web.notfound()
