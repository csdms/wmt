import web
import json

from ..render import render
from ..models import components as comps
from ..validators import (not_too_long, not_too_short, not_bad_json)


class List(object):
    def GET(self):
        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps(comps.get_component_names(sort=True))


class Show(object):
    def GET(self, name):
        web.header('Content-Type', 'application/json; charset=utf-8')
        user_data = web.input(key=None)
        try:
            comp = comps.get_component(name)
        except comps.IdError:
            raise web.notfound()

        if user_data.key is None:
            return json.dumps(comp)
        else:
            try:
                return json.dumps(comp[user_data.key])
            except KeyError:
                raise web.notfound()


class Dump(object):
    def GET(self):
        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps(comps.get_components())


class Parameters(object):
    def GET(self, name):
        web.header('Content-Type', 'application/json; charset=utf-8')
        try:
            return json.dumps(comps.get_component_params(name))
        except KeyError:
            raise web.notfound()


class Input(object):
    def GET(self, name):
        try:
            return render.code(comps.get_component_input(name))
        except KeyError as error:
            return error
            raise web.notfound()


class Format(object):
    form = web.form.Form(
        web.form.Textarea('json',
                          not_too_long(4096),
                          rows=40, cols=80, description=None),
        web.form.Textbox('format', size=40, description="Format"),
        web.form.Button('Submit')
    )

    def GET(self, name):
        x = web.input(defaults='false', format='html')
        if x['defaults'].lower() == 'true':
            mapping = comps.get_component_defaults(name)
            if x['format'].lower() == 'html':
                return render.files(comps.get_component_formatted_input(name, **mapping))
            elif x['format'].lower() == 'json':
                web.header('Content-Type', 'application/json; charset=utf-8')
                mapping.pop('separator')
                return json.dumps(mapping, sort_keys=True, indent=4, separators=(',', ': '))
            elif x['format'].lower() == 'text':
                files = comps.get_component_formatted_input(name, **mapping)
                return '\n'.join([
                    '>>> start: {0}\n{1}\n<<< end: {0}\n'.format(*item) for item in files.items()])
        else:
            return render.format(self.form())

    def POST(self, name):
        form = self.form()
        if not form.validates():
            return render.format(form)
        mapping = json.loads(form.d.json)
        return render.files(comps.get_component_formatted_input(name, **mapping))


class Defaults(object):
    def GET(self, name):
        web.header('Content-Type', 'application/json; charset=utf-8')
        try:
            return json.dumps(comps.get_component_defaults(name))
        except KeyError:
            raise web.notfound()


class Command(object):
    def GET(self, name):
        x = web.input(format='json')
        try:
            if x['format'] == 'json':
                web.header('Content-Type', 'application/json; charset=utf-8')
                return json.dumps(comps.get_component_argv(name))
            else:
                return render.code('> ' + ' '.join(comps.get_component_argv(name)))
        except KeyError:
            raise web.notfound()
