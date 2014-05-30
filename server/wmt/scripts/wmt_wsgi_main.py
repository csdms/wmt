#!/usr/bin/env python
import os
import sys
import web

os.environ.setdefault('WMT_ENABLE_SESSIONS', 'FALSE')
os.environ.setdefault('WMT_PREFIX',
    os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
)

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


app = web.application(URLS, globals())

if os.environ['WMT_ENABLE_SESSIONS'].upper() == 'TRUE':
    from wmt.session import add_sessions_to_app
    add_sessions_to_app(app)

application = app.wsgifunc(Log)
