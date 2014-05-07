import web
import json

from ..validators import not_too_short
from ..render import render

from ..models import tags
from ..session import get_username


class New(object):
    form = web.form.Form(
        web.form.Textbox('tag',
                         not_too_short(3),
                         size=30, description='Tag:'),
        web.form.Button('Submit')
    )

    def GET(self):
        return render.titled_form('New Tag', self.form())

    def POST(self):
        web.header('Content-Type', 'application/json; charset=utf-8')
        form = self.form()
        if not form.validates():
            return render.titled_form('New Tag', form)

        id = tags.new_tag(form.d.tag, owner=get_username())
        return json.dumps(tags.get_tag(id))
        #return json.dumps(id)


class Delete(object):
    def POST(self, id):
        web.header('Content-Type', 'application/json; charset=utf-8')
        tags.del_tag(id)
        return json.dumps(id)
        #raise web.seeother('/')


class Get(object):
    def GET(self, id):
        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps(tags.get_tag(id))


class List(object):
    def GET(self):
        web.header('Content-Type', 'application/json; charset=utf-8')

        all_tags = tags.get_tags()
        resp = []
        for tag in all_tags:
            resp.append(dict(id=tag.id, tag=tag.tag, owner=tag.owner))

        return json.dumps(resp)


class TagModel(object):
    form = web.form.Form(
        web.form.Textbox('model',
                         size=30, description='Model:'),
        web.form.Textbox('tag',
                         size=30, description='Tag:'),
        web.form.Button('Submit')
    )
    def GET(self):
        return render.titled_form('Tag Model', self.form())

    def POST(self):
        web.header('Content-Type', 'application/json; charset=utf-8')

        form = self.form()
        if not form.validates():
            return render.titled_form('Tag Model', form)

        tags.tag_model(form.d.model, form.d.tag)
        return json.dumps(tags.get_model_tags(form.d.model))


class Query(object):
    def GET(self):
        web.header('Content-Type', 'application/json; charset=utf-8')
        params = web.input(tags='')
        models = tags.select_model(params.tags.split(','))
        return json.dumps(list(models))
