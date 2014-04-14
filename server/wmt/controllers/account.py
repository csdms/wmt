import web
import json

from ..validators import not_too_long, valid_email_address
from ..render import render
from ..models import users
from ..session import (login, logout, get_username)
from ..config import site


class Login(object):
    form = web.form.Form(
        web.form.Textbox('username',
                         not_too_long(512),
                         valid_email_address,
                         size=30, description='username:'),
        web.form.Password('password',
                          web.form.notnull,
                          size=30,
                          description='password:'),
        web.form.Button('Login')
    )

    def GET(self):
        return render.titled_form('login', self.form())

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.login(form)

        user = users.get_user(form.d.username)
        if user is None:
            users.new_user(form.d.username, form.d.password)
            user = users.get_user(form.d.username)

        if site['pw'].verify(form.d.password, user.password):
            login(form.d.username)
        else:
            raise web.Unauthorized()

        raise web.seeother('/')


class Logout(object):
    def GET(self):
        logout()
        raise web.seeother('/')

    def POST(self):
        logout()
        raise web.seeother('/')


class Username(object):
    def GET(self):
        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps(get_username())
