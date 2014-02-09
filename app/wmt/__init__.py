#!/usr/bin/env python

from __future__ import generators

__version__ = "0.1"
__author__ = [
    "Eric Hutton <eric.hutton@colorado.edu>",
]
__license__ = "public domain"

URLS = (
    '/', 'wmt.controllers.index.Index',
    '/help', 'wmt.controllers.index.Help',
    '/help/(.*)', 'wmt.controllers.index.Help',
    '/show', 'wmt.controllers.actions.Show',

    '/login', 'wmt.controllers.account.Login',
    '/logout', 'wmt.controllers.account.Logout',

    '/components/list', 'wmt.controllers.components.List',
    '/components/dump', 'wmt.controllers.components.Dump',
    '/components/show/(\w+)', 'wmt.controllers.components.Show',

    '/models/new', 'wmt.controllers.models.New',
    '/models/open/(\d+)', 'wmt.controllers.models.Open',
    '/models/save/(\d+)', 'wmt.controllers.models.Save',
    '/models/list', 'wmt.controllers.models.List',
    '/models/delete/(\d+)', 'wmt.controllers.models.Delete',
    '/models/edit/(\d+)', 'wmt.controllers.models.Edit',
    '/models/view/(\d+)', 'wmt.controllers.models.View',
    '/models/show/(\d+)', 'wmt.controllers.models.Show',
    '/models/export/(\d+)', 'wmt.controllers.models.Export',

    '/models/convert', 'wmt.controllers.actions.Convert',
    '/models/submit', 'wmt.controllers.actions.Submit',

    '/hosts/new', 'wmt.controllers.hosts.New',
    '/hosts/view/(\d+)', 'wmt.controllers.hosts.View',
    '/hosts/edit/(\d+)', 'wmt.controllers.hosts.Edit',
    '/hosts/run/(\d+)', 'wmt.controllers.hosts.Run',
)


from . import models
from . import scripts

