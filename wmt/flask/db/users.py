from flask import current_app

from sqlalchemy import Column, Integer, Text

from passlib.context import CryptContext

from ..services import db
from ..database import SESSIONS
from ..utils import as_resource, as_collection
from ..services import Service

Base = SESSIONS['users']['base']
db_session = SESSIONS['users']['session']

#pw = CryptContext.from_path(current_app.config['CRYPT_INI_PATH'],
#                            section='passlib')


class Username(db.Model):
    __tablename__ = 'users'
    __bind_key__ = 'users'

    id = Column(Integer, primary_key=True)
    username = Column(Text, unique=True)
    password = Column(Text)

    def __init__(self, username, password):
        self.username = username
        self.password = password

    def __repr__(self):
        return '<User %r>' % self.username


class UsersService(Service):
    __model__ = Username

    def contains(self, needle):
        return db.query.filter(db.username.like('%' + needle + '%'))

    def change_password(name, old, new):
        if authenticate_user(name, old):
            user = self.get_by_name(name)
            password = current_app.config['pw'].encrypt(new)
            self.update(user, password=password)
            return True
        return False

    def get_by_name(self, name):
        return self.first(username=name)

    def authenticate(self, user, password):
        return current_app.config['pw'].verify(password, user.password)


users = UsersService()


def add(name, password):
    user = get_by_name(name)

    if user is None:
        #db_session.add(Username(name, pw.encrypt(password)))
        db_session.add(Username(name,
                                current_app.config['pw'].encrypt(password)))
        db_session.commit()
    else:
        abort(418)

    return user


def all(sort=None, order=None):
    if sort is None:
        names = Username.query.all()
    else:
        names = Username.query.order_by(getattr(Username, sort))

    if order == 'asc':
        return names
    else:
        return names[::-1]


def get(id):
    return Username.query.get(id)


def get_by_name(name):
    return Username.query.filter_by(username=name).first()


def contains(needle):
    return Username.query.filter(Username.username.like('%' + needle + '%'))


def change_password(name, old, new):
    if authenticate_user(name, old):
        user = get_by_name(name)
        #user.password = pw.encrypt(new)
        user.password = current_app.config['pw'].encrypt(new)
        db_session.commit()
        return True
    return False
