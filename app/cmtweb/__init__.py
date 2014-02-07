#!/usr/bin/env python

from __future__ import generators

__version__ = "0.1"
__author__ = [
    "Eric Hutton <eric.hutton@colorado.edu>",
]
__license__ = "public domain"

URLS = (
    '/', 'cmtweb.controllers.index.Index',
    '/help', 'cmtweb.controllers.index.Help',
    '/help/(.*)', 'cmtweb.controllers.index.Help',
    '/show', 'cmtweb.controllers.actions.Show',

    '/login', 'cmtweb.controllers.account.Login',
    '/logout', 'cmtweb.controllers.account.Logout',

    '/models/new', 'cmtweb.controllers.models.New',
    '/models/delete/(\d+)', 'cmtweb.controllers.models.Delete',
    '/models/edit/(\d+)', 'cmtweb.controllers.models.Edit',
    '/models/view/(\d+)', 'cmtweb.controllers.models.View',
    '/models/show/(\d+)', 'cmtweb.controllers.models.Show',
    '/models/export/(\d+)', 'cmtweb.controllers.models.Export',

    '/models/convert', 'cmtweb.controllers.actions.Convert',
    '/models/submit', 'cmtweb.controllers.actions.Submit',

    '/hosts/new', 'cmtweb.controllers.hosts.New',
    '/hosts/view/(\d+)', 'cmtweb.controllers.hosts.View',
    '/hosts/edit/(\d+)', 'cmtweb.controllers.hosts.Edit',
    '/hosts/run/(\d+)', 'cmtweb.controllers.hosts.Run',
)


from . import models
from . import scripts

