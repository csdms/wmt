import web
import json

from ..validators import not_too_long, valid_email_address
from ..render import render
from ..models import users
from ..session import (login, logout, get_username)
from ..config import site
from ..decorators import as_json


class Users(object):
    @as_json
    def GET(self):
        return [dict(user) for user in users.get_users()]

    @as_json
    def POST(self):
        payload = json.loads(web.data())

        user = users.get_user(payload["username"])
        if user:
            raise web.notfound("user already exists")
        else:
            id = users.new_user(payload["username"], payload["password"])

        return id


class User(object):
    @as_json
    def GET(self, id):
        user = users.get_user_by_id(id)
        if user is None:
            raise web.notfound(
                json.dumps(
                    dict(message="Not found",
                         documentation_url="https://github.com/csdms/wmt"),
                    sort_keys=True, indent=4, separators=(',', ': ')))
        else:
            return dict(user)

    @as_json
    def REMOVE(self, id):
        users.delete_user(id)

    @as_json
    def PATCH(self, id):
        payload = json.loads(web.data())

        password = payload['password']

        if len(password) == 0:
            raise web.BadRequest('password must not be null')

        users.change_password(id, password)


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
        return render.login(self.form())

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.login(form)

        user = users.get_user(form.d.username)
        if user is None:
            raise web.BadRequest('user does not exist')

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
