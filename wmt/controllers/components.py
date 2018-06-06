import web
import json

from ..render import render
from ..models import components as comps
from ..validators import (not_too_long, not_too_short, not_bad_json)


indent = 2


def refresh_component(name):
    hooks = comps.get_component_hooks(name)
    hooks['refresh'].execute(name)


class List(object):
    def GET(self):
        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps(comps.get_component_names(sort=True), indent=indent)


class PrettyList(object):
    def GET(self):
        components = comps.get_components()
        components.sort(cmp=lambda a, b: cmp(a['id'], b['id']))
        return render.componenttable(components)


class Show(object):
    def GET(self, name):
        web.header('Content-Type', 'application/json; charset=utf-8')
        user_data = web.input(key=None)
        try:
            comp = comps.get_component(name)
        except comps.IdError:
            raise web.notfound()

        if user_data.key is None:
            return json.dumps(comp, indent=indent)
        else:
            try:
                return json.dumps(comp[user_data.key], indent=indent)
            except KeyError:
                raise web.notfound()


class PrettyShow(object):
    def GET(self, name):
        try:
            component = comps.get_component(name)
        except comps.IdError:
            raise web.notfound()
        return render.component(component)


class Dump(object):
    def GET(self):
        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps(comps.get_components(), indent=indent)


class Parameters(object):
    def GET(self, name):
        web.header('Content-Type', 'application/json; charset=utf-8')
        try:
            return json.dumps(comps.get_component_params(name), indent=indent)
        except KeyError:
            raise web.notfound()


class PrettyParameters(object):
    def GET(self, name):
        try:
            parameters = comps.get_component_params(name)
        except KeyError:
            raise web.notfound()

        tables = []
        for param in parameters:
            if param['key'] == 'separator':
                table = []
                tables.append((param['name'], table))
            else:
                try:
                    table.append(param)
                except NameError:
                    table = []
                    tables.append(('', table))


        return render.paramtable(tables)


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
                          not_too_long(10240),
                          rows=40, cols=80, description=None),
        web.form.Button('Submit')
    )

    def GET(self, name):
        return render.format(self.form())

    def POST(self, name):
        form = self.form()
        if not form.validates():
            return render.format(form)
        mapping = json.loads(form.d.json)

        files = comps.get_component_formatted_input(name, **mapping)
        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps(files)


class Defaults(object):
    def GET(self, name):
        web.header('Content-Type', 'application/json; charset=utf-8')
        try:
            return json.dumps(comps.get_component_defaults(name), indent=indent)
        except KeyError:
            raise web.notfound()


class Command(object):
    def GET(self, name):
        x = web.input(format='json')
        try:
            if x['format'] == 'json':
                web.header('Content-Type', 'application/json; charset=utf-8')
                return json.dumps(comps.get_component_argv(name), indent=indent)
            else:
                return render.code('> ' + ' '.join(comps.get_component_argv(name)))
        except KeyError:
            raise web.notfound()


class Refresh(object):
    def GET(self, name):
        refresh_component(name)

        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps('{} component refreshed'.format(name))


class RefreshAll(object):
    def GET(self):
        component_names = comps.get_component_names(sort=True)
        for name in component_names:
            refresh_component(name)

        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps('All components refreshed')
