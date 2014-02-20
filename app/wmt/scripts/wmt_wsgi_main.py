#!/usr/bin/env python
import os
import web

os.environ.setdefault('WMT_PREFIX',
    os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
)
from wmt import URLS
from wmt.session import add_sessions_to_app

from wsgilog import WsgiLog

class Log(WsgiLog):
    def __init__(self, application):
        WsgiLog.__init__(
            self,
            application,
            logformat = '%(message)s',
            tofile = True,
            toprint = True,
            file = os.path.join(os.environ['WMT_PREFIX'], 'logs', 'wsgilog.log'),
            interval = 'S',
            backups = 5,
        )


def not_found():
    return web.notfound("Sorry, the page you were looking for was not found.")


app = web.application(URLS, globals())
app.notfound = not_found

add_sessions_to_app(app)

application = app.wsgifunc(Log)
