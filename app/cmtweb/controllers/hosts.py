import web

from ..validators import not_too_long, valid_email_address
from ..render import render
from ..models import hosts
from ..session import (login, logout, get_session)


def run_command_on_server(username, host, command, password=''):
    import paramiko

    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    try:
        ssh.connect(host, username=username, password=password)
    except paramiko.AuthenticationException:
        return 'Authentication error'
    except paramiko.SSHException:
        return 'Ssh error: ssh %s@%s:%s' % (username, host, password)

    stdin, stdout, stderr = ssh.exec_command(command)
    return ''.join(stdout.readlines())


class New(object):
    form = web.form.Form(
        web.form.Textbox('host',
                         not_too_long(512),
                         size=30, description='host:'),
        web.form.Textbox('username',
                         not_too_long(512),
                         size=30, description='username:'),
        web.form.Password('password',
                          web.form.notnull,
                          size=30,
                          description='password:'),
        web.form.Textbox('command',
                         not_too_long(512),
                         size=30, description='command:'),
        web.form.Button('Save')
    )

    def GET(self):
        return render.new_host(self.form())
        #return render.submit(self.form())

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.new_host(form)

        hosts.new_host(form.d.host, form.d.username,
                       command=form.d.command,
                       password=form.d.password)

        raise web.seeother('/')


class Edit(object):
    def GET(self, id):
        host = hosts.get_host(int(id))
        form = New.form()
        form.fill(host)
        return render.edit_host(host, form)

    def POST(self, id):
        form = New.form()
        host = hosts.get_host(id)
        if not form.validates():
            return render.edit_host(host, form)
        hosts.update_host(id, host=form.d.host, username=form.d.username,
                          password=form.d.password, command=form.d.command)
        raise web.seeother('/')


class View(object):
    form = web.form.Form(
        web.form.Textbox('host',
                         not_too_long(512),
                         size=30, description='host:',
                         disabled='on'),
        web.form.Textbox('username',
                         not_too_long(512),
                         size=30, description='username:',
                         disabled='on'),
        web.form.Textbox('command',
                         not_too_long(512),
                         size=30, disabled='on',
                         description='command:'),
    )

    def GET(self, id):
        host = hosts.get_host(int(id))
        self.form.fill(host)
        return render.edit_host(host, self.form)


class Run(object):
    form = web.form.Form(
        web.form.Textbox('host',
                         not_too_long(512),
                         size=30, description='host:'),
        web.form.Textbox('username',
                         not_too_long(512),
                         size=30, description='username:'),
        web.form.Password('password',
                          web.form.notnull,
                          size=30,
                          description='password:'),
        web.form.Textbox('command',
                         not_too_long(512),
                         size=30, description='command:'),
        web.form.Button('Run')
    )

    def GET(self, id):
        host = hosts.get_host(int(id))
        self.form.fill(host)
        return render.edit_host(host, self.form)

    def POST(self, id):
        host = hosts.get_host(int(id))
        form = self.form()
        if not form.validates():
            return render.edit_host(host, form)

        result = run_command_on_server(form.d.username, form.d.host,
                                       form.d.command,
                                       password=form.d.password)

        return render.code(result)


