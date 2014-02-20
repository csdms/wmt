import web
import json

from ..models import (models, users)
from ..render import render
from ..validators import (not_too_short, not_bad_json)
from ..cca import rc_from_json


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
        form = self.form()
        if not form.validates():
            return render.new(form)
        id = models.new_model(form.d.name, form.d.json, owner='')
        return json.dumps(id)


class Save(object):
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

        return json.dumps(int(id))


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
        model = models.get_model(int(id))
        form = New.form()
        form.fill(model)
        return render.edit(model, form)

    def POST(self, id):
        form = New.form()
        model = models.get_model(id)
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
        model = models.get_model(id)
        return render.view(model)


class Open(object):
    def GET(self, id):
        try:
            model = models.get_model(id)
        except models.BadIdError:
            raise web.notfound()
        else:
            return json.dumps(dict(name=model.name, id=model.id,
                                   owner=model.owner))


class Show(object):
    def GET(self, id):
        try:
            model = models.get_model(str(id))
        except models.BadIdError:
            raise web.notfound()
        else:
            return model.json


class List(object):
    def GET(self):
        all_models = models.get_models()
        resp = []
        for model in all_models:
            resp.append(dict(id=model.id, name=model.name))
        return json.dumps(resp)


class Export(object):
    """
    Export a saved model as a CMT resource file by model *id*. To export
    model *1* to RC format,

    * https://csdms.colorado.edu/wmt/export/1
    """
    def GET(self, id):
        model = models.get_model(id)
        return render.code(rc_from_json(model.json))
