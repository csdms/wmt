import web

from ..models import names
from ..models.names import to_resource
from ..decorators import as_json


class Names(object):
    @as_json
    def GET(self):
        return [to_resource(name) for name in names.get_names()]

    @as_json
    def OPTIONS(self):
        web.header('Allow', 'GET')


class Name(object):
    @as_json
    def GET(self, id):
        return to_resource(names.get_name(id))

    @as_json
    def OPTIONS(self):
        web.header('Allow', 'GET')
