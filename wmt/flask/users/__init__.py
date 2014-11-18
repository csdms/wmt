from flask import current_app

from ..core import Service, db
from .models import User


class UsersService(Service):
    __model__ = User

    def create(self, username, password):
        encrypted = current_app.config['pw'].encrypt(password)
        return super(UsersService, self).create(username, encrypted)

    def contains(self, needle):
        return db.query.filter(db.username.like('%' + needle + '%'))

    def change_password(name, old, new):
        if authenticate_user(name, old):
            user = self.get_by_name(name)
            password = current_app.config['pw'].encrypt(new)
            self.update(user, password=password)
            return True
        return False

    #def get_by_name(self, name):
    #    return self.first(username=name)

    def authenticate(self, username, password):
        user = self.first(username=username)
        if user is not None:
            return current_app.config['pw'].verify(password, user.password)
        else:
            return False
