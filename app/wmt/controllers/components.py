import web
import json

from ..render import render

from ..validators import (not_too_long, not_bad_json, )
from ..cca import rc_from_json
from ..palette import PALETTE


class List(object):
    def GET(self):
        return json.dumps(PALETTE.keys())


class Show(object):
    def GET(self, name):
        try:
            return json.dumps(PALETTE[name])
        except KeyError:
            raise web.notfound()


class Dump(object):
    def GET(self):
        return json.dumps(PALETTE)
