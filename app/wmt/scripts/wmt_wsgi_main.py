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


app = web.application(URLS, globals())

add_sessions_to_app(app)

application = app.wsgifunc()
