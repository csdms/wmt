import web
import json
import os
import shutil
from string import Formatter
from datetime import datetime

import yaml

from ..models import (models, users, components, submissions)
from ..render import render
from ..validators import (not_too_short, not_bad_json)
from ..cca import rc_from_json
from ..config import site
from ..utils.io import chunk_copy
from ..session import get_username

from collections import namedtuple


Status = namedtuple('Status', ['status', 'message'])


def _get_model_or_raise(id):
    try:
        return models.get_model(int(id))
    except models.BadIdError:
        raise web.notfound()
    except models.AuthorizationError:
        raise web.Unauthorized()


def _copy_uploaded_files(id, dest):
    upload_dir = models.get_model_upload_dir(id)
    files = os.listdir(upload_dir)
    for f in files:
        shutil.copy(os.path.join(upload_dir, f), dest)


def get_generated_input(name, path):
    files = [f for f in os.listdir(path) if not
             os.path.isdir(os.path.join(path, f))]

    input = dict()
    format = Formatter()

    for f in files:
        with open(os.path.join(path, f), 'r') as fp:
            contents = fp.read()
        if components.is_text(contents):
            input[f] = format.format(contents)

    return input


class Validate(object):
    form = web.form.Form(
        web.form.Textarea('json',
                          rows=40, cols=80, description=None),
        web.form.Button('Validate')
    )

    def GET(self):
        return render.titled_form('JSON Lint', self.form())

    def POST(self):
        web.header('Content-Type', 'text/html; charset=utf-8')
        form = self.form()
        if not form.validates():
            return render.new(form)
        try:
            json.loads(form.d.json)
        except ValueError as error:
            return render.error(str(error))
            # return render.error(Status('error', str(error)))
        return render.status('Success', Status('success', 'Valid JSON'))


class New(object):
    """
    Create a new model. To create a new model, go here:

    * https://csdms.colorado.edu/wmt/models/new

    You could also just POST some JSON to this URL.

    A valid JSON description of a model is simply a valid JSON file
    of an object with a member called *model*. The simplest
    example would be::

    { "model": 0 }

    > curl -i -X POST -F name=ModelName -F json=@model.json https://csdms.colorado.edu/wmt/models/new
    """
    form = web.form.Form(
        web.form.Textbox('name',
                         not_too_short(3),
                         #not_too_long(20),
                         size=30, description='Model name:'),
        web.form.Textarea('json',
                          #not_too_long(2048),
                          not_bad_json,
                          rows=40, cols=80, description=None),
        web.form.Button('Save')
    )

    def GET(self):
        return render.new(self.form())

    def POST(self):
        web.header('Content-Type', 'application/json; charset=utf-8')
        form = self.form()
        if not form.validates():
            return render.new(form)
        id = models.new_model(form.d.name, form.d.json,
                              owner=get_username())
        return json.dumps(id)


class Save(object):
    form = web.form.Form(
        web.form.Textbox('name',
                         not_too_short(3),
                         size=30, description='Model name:'),
        web.form.Textarea('json',
                          not_bad_json,
                          rows=40, cols=80, description=None),
        web.form.Button('Save')
    )
    def GET(self, id):
        try:
            model = models.get_model(int(id))
        except models.BadIdError:
            return render.new(self.form())
        else:
            self.form.fill(model)
            return render.edit(model, self.form)

    def POST(self, id):
        form = self.form()
        if not form.validates():
            return render.new(form)
        try:
            model = models.get_model(id)
        except models.BadIdError:
            id = models.new_model(form.d.name, form.d.json, owner='')
        else:
            models.update_model(id, form.d.name, form.d.json)

        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps(int(id))


class SaveAs(New):
    def GET(self, id):
        return New.GET(self)

    def POST(self, id):
        new_id = New.POST(self)

        src = models.get_model_upload_dir(id)
        dst = models.get_model_upload_dir(new_id)

        for name in os.listdir(src):
            srcname = os.path.join(src, name)
            dstname = os.path.join(dst, name)
            shutil.copy2(srcname, dstname)

        return new_id


class Delete(object):
    """
    Remove a model by *id*. To remove model *1*, go here (**please don't do
    this unless it's a model you created!**):

    * https://csdms.colorado.edu/wmt/models/remove/1
    """
    def POST(self, id):
        models.del_model(int(id))
        raise web.seeother('/')


class Edit(object):
    """
    Edit a model by model *id*. To edit model *1*, go here:

    * https://csdms.colorado.edu/wmt/models/view/1

    You could also just POST some JSON to this URL.
    """
    def GET(self, id):
        model = _get_model_or_raise(id)
        form = New.form()
        form.fill(model)
        return render.edit(model, form)

    def POST(self, id):
        form = New.form()
        model = _get_model_or_raise(id)
        if not form.validates():
            return render.edit(model, form)
        models.update_model(id, form.d.name, form.d.json)
        raise web.seeother('/')


class View(object):
    """
    View a model description by model *id*. To view the model with id *1*
    you would go to this page,

    * https://csdms.colorado.edu/wmt/models/view/1
    """
    def GET(self, id):
        model = _get_model_or_raise(id)
        as_yaml = yaml.dump(yaml.load(model.json), default_flow_style=False)
        return render.view(model, as_yaml)


class Open(object):
    def GET(self, id):
        web.header('Content-Type', 'application/json; charset=utf-8')
        model = _get_model_or_raise(id)
        date = datetime.strptime(model.date, '%a, %d %b %Y %H:%M:%S %Z')
        return json.dumps(dict(name=model.name, id=model.id,
                               owner=model.owner, date=date.isoformat()))


class Show(object):
    def GET(self, id):
        web.header('Content-Type', 'application/json; charset=utf-8')
        model = _get_model_or_raise(id)
        return model.json


class List(object):
    def GET(self):
        web.header('Content-Type', 'application/json; charset=utf-8')
        all_models = models.get_models(sortby='name')
        resp = []
        for model in all_models:
            date = datetime.strptime(model.date, '%a, %d %b %Y %H:%M:%S %Z')
            resp.append(dict(id=model.id, name=model.name,
                             owner=model.owner, date=date.isoformat()))
        return json.dumps(resp)


class PrettyList(object):
    def GET(self):
        all_models = models.get_models(sortby='name')
        models_brief = []
        for model in all_models:
            date = datetime.strptime(model.date, '%a, %d %b %Y %H:%M:%S %Z')
            models_brief.append(dict(id=model.id, name=model.name,
                                     owner=model.owner, date=date.isoformat()))
        models_brief.sort(cmp=lambda a, b: cmp(a['date'], b['date']))
        models_brief.reverse()
        return render.modeltable(models_brief)


class Export(object):
    """
    Export a saved model as a CMT resource file by model *id*. To export
    model *1* to RC format,

    * https://csdms.colorado.edu/wmt/export/1
    """
    def GET(self, id):
        model = _get_model_or_raise(id)
        return render.code(rc_from_json(model.json))


_CHUNK_SIZE = 10240


class Upload(object):
    def GET(self):
        return render.uploadform("id")

    def POST(self):
        web.header('Content-Type', 'application/json; charset=utf-8')

        user_data = web.input(file={}, id=None, filename=None)

        if user_data['filename'] is None or len(user_data['filename']) == 0:
            filename = user_data['file'].filename
        else:
            filename = user_data['filename']

        model_upload_dir = models.get_model_upload_dir(user_data['id'])
        path_to_dest = os.path.join(model_upload_dir, filename)

        with open(path_to_dest, 'w') as dest_fp:
            checksum = chunk_copy(user_data['file'].file, dest_fp,
                                  chunk_size=_CHUNK_SIZE)

        return json.dumps({'checksum': checksum.hexdigest()})


class Format(object):
    def GET(self, id, name):
        x = web.input(defaults='false', format='html')

        if int(id) in [-1, 0]:
            mapping = components.get_component_defaults(name)
        else:
            try:
                component = models.get_model_component(int(id), name)
                tmpdir = submissions.stage_component(component,
                                                   prefix=site['tmp'],
                                                   hooks_only=True)
                _copy_uploaded_files(id, tmpdir)
                generated_input = get_generated_input(name, tmpdir)
                shutil.rmtree(tmpdir)
            except models.BadIdError:
                raise web.notfound()
            except models.AuthorizationError:
                raise web.Unauthorized()

            mapping = component['parameters']

        if x['format'].lower() == 'json':
            web.header('Content-Type', 'application/json; charset=utf-8')
            return json.dumps(mapping, sort_keys=True, indent=4,
                              separators=(',', ': '))
        else:
            files = components.get_component_formatted_input(name,
                                                             ignore_binary=True,
                                                             **mapping)
            combined = files.copy()
            if int(id) > 0:
                combined.update(generated_input)

            if x['format'].lower() == 'html':
                return render.files(combined)
            else:
                return '\n'.join(['>>> start: {0}\n{1}\n<<< end: {0}\n'
                                  .format(*item) for item in combined.items()])
