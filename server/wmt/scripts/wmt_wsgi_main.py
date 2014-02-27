#!/usr/bin/env python
import os
import sys
import web

os.environ.setdefault('WMT_ENABLE_SESSIONS', 'FALSE')
os.environ.setdefault('WMT_PREFIX',
    os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
)

prefix = '/home/faculty/huttone/Development/wmt/server'
if prefix not in sys.path:
    sys.path.insert(0, prefix)

from wmt import URLS

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

if os.environ['WMT_ENABLE_SESSIONS'].upper() == 'TRUE':
    from wmt.session import add_sessions_to_app
    add_sessions_to_app(app)

application = app.wsgifunc(Log)
