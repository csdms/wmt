#!/usr/bin/env python

from __future__ import generators

__version__ = "0.1"
__author__ = [
    "Eric Hutton <eric.hutton@colorado.edu>",
]
__license__ = "public domain"

_UUID_REGEX = '[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}'
URLS = (
    '/', 'wmt.controllers.index.Index',
    '/help', 'wmt.controllers.index.Help',
    '/help/(.*)', 'wmt.controllers.index.Help',
    '/show', 'wmt.controllers.actions.Show',

    '/account/login', 'wmt.controllers.account.Login',
    '/account/new', 'wmt.controllers.account.New',
    '/account/logout', 'wmt.controllers.account.Logout',
    '/account/username', 'wmt.controllers.account.Username',
    '/account/reset', 'wmt.controllers.account.ResetPassword',

    '/tag/new', 'wmt.controllers.tag.New',
    '/tag/delete/(\d+)', 'wmt.controllers.tag.Delete',
    '/tag/get/(\d+)', 'wmt.controllers.tag.Get',
    '/tag/list', 'wmt.controllers.tag.List',

    '/tag/model/add', 'wmt.controllers.tag.TagModel',
    '/tag/model/remove', 'wmt.controllers.tag.UntagModel',
    '/tag/model/query', 'wmt.controllers.tag.Query',
    '/tag/model/(\d+)', 'wmt.controllers.tag.ModelTags',

    '/components/pretty-list', 'wmt.controllers.components.PrettyList',
    '/components/pretty-show/(\w+)', 'wmt.controllers.components.PrettyShow',
    '/components/pretty-params/(\w+)', 'wmt.controllers.components.PrettyParameters',

    '/components/list', 'wmt.controllers.components.List',
    '/components/dump', 'wmt.controllers.components.Dump',
    '/components/show/(\w+)', 'wmt.controllers.components.Show',
    '/components/params/(\w+)', 'wmt.controllers.components.Parameters',
    '/components/defaults/(\w+)', 'wmt.controllers.components.Defaults',
    '/components/input/(\w+)', 'wmt.controllers.components.Input',
    '/components/format/(\w+)', 'wmt.controllers.components.Format',
    '/components/command/(\w+)', 'wmt.controllers.components.Command',

    '/models/pretty-list', 'wmt.controllers.models.PrettyList',

    '/models/new', 'wmt.controllers.models.New',
    '/models/open/(\d+)', 'wmt.controllers.models.Open',
    '/models/save/(\d+)', 'wmt.controllers.models.Save',
    '/models/saveas/(\d+)', 'wmt.controllers.models.SaveAs',
    '/models/list', 'wmt.controllers.models.List',
    '/models/delete/(\d+)', 'wmt.controllers.models.Delete',
    '/models/edit/(\d+)', 'wmt.controllers.models.Edit',
    '/models/view/(\d+)', 'wmt.controllers.models.View',
    '/models/show/(\d+)', 'wmt.controllers.models.Show',
    '/models/export/(\d+)', 'wmt.controllers.models.Export',
    '/models/upload', 'wmt.controllers.models.Upload',
    '/models/validate', 'wmt.controllers.models.Validate',
    '/models/(-?\d+)/(\w+)/format', 'wmt.controllers.models.Format',

    '/models/convert', 'wmt.controllers.actions.Convert',
    '/models/submit', 'wmt.controllers.actions.Submit',

    '/run/new', 'wmt.controllers.run.New',
    '/run/delete/(%s)' % _UUID_REGEX, 'wmt.controllers.run.Delete',
    '/run/show', 'wmt.controllers.run.Show',
    '/run/status', 'wmt.controllers.run.Status',
    '/run/stage', 'wmt.controllers.run.Stage',
    '/run/launch', 'wmt.controllers.run.Launch',
    '/run/update', 'wmt.controllers.run.Update',
    '/run/upload', 'wmt.controllers.run.Upload',
    '/run/upload/(%s)' % _UUID_REGEX, 'wmt.controllers.run.UploadUuid',
    '/run/download/(%s)/(.+)' % _UUID_REGEX, 'wmt.controllers.run.Download',
    '/run/download', 'wmt.controllers.run.DownloadBundle',
    '/run/(%s)' % _UUID_REGEX, 'wmt.controllers.run.Get',
    '/run/(%s)/status' % _UUID_REGEX, 'wmt.controllers.run.Status',
    '/run/', 'wmt.controllers.run.GetAll',
    '/run/visualize', 'wmt.controllers.run.Visualize',

    '/run/delete/ui/(%s)' % _UUID_REGEX, 'wmt.controllers.run.UiDelete',

    '/hosts/new', 'wmt.controllers.hosts.New',
    '/hosts/view/(\d+)', 'wmt.controllers.hosts.View',
    '/hosts/edit/(\d+)', 'wmt.controllers.hosts.Edit',
    '/hosts/run/(\d+)', 'wmt.controllers.hosts.Run',
)

