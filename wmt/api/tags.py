import web
import json

from ..validators import not_too_short
from ..render import render

from ..models import tags
from ..models.tags import to_resource
from ..session import get_username
from ..decorators import as_json


class Tags(object):
    @as_json
    def GET(self):
        data = web.input(template=False)
        if data.template is not False:
            return dict(tag='name-of-tag', owner='owner-of-tag')
        return [to_resource(tag) for tag in tags.get_tags()]

    @as_json
    def POST(self):
        payload = json.loads(web.data())

        id = tags.new_tag(payload['tag'], owner=get_username())
        return to_resource(tags.get_tag(id))

    @as_json
    def OPTIONS(self):
        web.header('Allow', 'GET, POST')


class Tag(object):
    @as_json
    def GET(self, id):
        return to_resource(tags.get_tag(id))

    @as_json
    def REMOVE(self, id):
        tags.del_tag(id)

    @as_json
    def OPTIONS(self):
        web.header('Allow', 'GET, REMOVE')


