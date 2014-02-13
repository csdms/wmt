import web

from ..render import render

from ..validators import (not_too_long, not_bad_json, )
from ..cca import rc_from_json
from ..models.components import (get_component_names, get_component,
                                 IdError)


class Convert(object):
    """
    Convert a JSON description of a model into a CMT resource file::

        curl -i -X POST -d @model.json https://csdms.colorado.edu/wmt/convert
    """
    def GET(self):
        return render.convert()

    def POST(self):
        return rc_from_json(web.data())


class RcFile(object):
    form = web.form.Form(
        web.form.Textarea('content',
                          not_too_long(2048),
                          not_bad_json,
                          rows=40, cols=80, description=None),
        web.form.Button('submit', type='submit', description='Submit',
                        html='Get RC File'),
    )

    def GET(self):
        return render.rc(self.form())

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.rc(form)
        return render.code(rc_from_json(form.d.content))


class Show(object):
    """
    Go here to show information about a specific element. The default is to
    show a list of all the components in the palette. Thus, the following are
    equivalent,

    * https://csdms.colorado.edu/wmt/show
    * https://csdms.colorado.edu/wmt/show?element=palette

    To show a JSON description of a particular component, specify it with the
    element attribute,

    * https://csdms.colorado.edu/wmt/show?element=avulsion

    To show a list of all the available models,

    * https://csdms.colorado.edu/wmt/show?element=models
    """
    def GET(self):
        user_data = web.input(element='palette')
        if user_data.element == 'palette':
            return json.dumps(get_component_names())
        elif user_data.element == 'models':
            return json.dumps(
                [dict(id=p.id, name=p.name) for p in models.get_models()])
        elif user_data.element == 'users':
            return json.dumps([u.username for u in users.get_users()])
        else:
            try:
                return json.dumps(get_component(user_data.element))
            except IdError:
                return '%s: Unknown element' % user_data.element


submit_form = web.form.Form(
    web.form.Textbox('username',
                     not_too_long(512),
                     size=30, description='username:'),
    web.form.Password('password',
                      web.form.notnull,
                      size=30,
                      description='password:'),
    web.form.Textbox('host',
                     not_too_long(512),
                     size=30, description='host:'),
    web.form.Textbox('command',
                     not_too_long(512),
                     size=30, description='command:'),
    web.form.Button('Submit')
)


def run_command_on_server(username, host, command, password=''):
    import paramiko

    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    try:
        ssh.connect(host, username=username, password=password)
    except paramiko.AuthenticationException:
        return 'Authentication error'

    stdin, stdout, stderr = ssh.exec_command(command)
    return ''.join(stdout.readlines())


class Submit(object):
    def GET(self):
        return render.submit(submit_form())

    def POST(self):
        form = submit_form()
        if not form.validates():
            return render.submit(form)

        result = run_command_on_server(form.d.username, form.d.host,
                                       form.d.command,
                                       password=form.d.password)

        return render.code(result)
