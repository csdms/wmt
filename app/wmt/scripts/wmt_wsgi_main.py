#!/usr/bin/env python
import os
import web

os.environ.setdefault('WMT_PREFIX',
    os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
)
from wmt import URLS
import wmt.config
from wmt.session import add_sessions_to_app
import wmt.controllers


def not_found():
    return web.notfound("Sorry, the page you were looking for was not found.")


app = web.application(URLS, globals())
app.notfound = not_found

add_sessions_to_app(app)

application = app.wsgifunc()
