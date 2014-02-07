#!/usr/bin/env python
import os
import web

os.environ.setdefault('CMTWEB_PREFIX',
    os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
)
from cmtweb import URLS
import cmtweb.config
from cmtweb.session import add_sessions_to_app
import cmtweb.controllers


app = web.application(URLS, globals())

add_sessions_to_app(app)

application = app.wsgifunc()
