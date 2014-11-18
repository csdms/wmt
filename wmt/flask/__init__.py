import os

from flask import Flask
from flask_login import LoginManager

from passlib.context import CryptContext

from .core import db
from .blueprints import register_blueprints
from .errors import ERROR_HANDLERS


class User(object):
    def __init__(self, id):
        self._id = id

    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    def get_id(self):
        return self._id


def create_app(settings_override=None, register_security_blueprint=True):
    app = Flask(__name__, instance_relative_config=True)

    login_manager = LoginManager()
    login_manager.init_app(app)

    @login_manager.user_loader
    def load_user(userid):
        return User(userid)

    @app.before_first_request
    def create_database():
        db.create_all()

    app.config.from_object('wmt.flask.settings')
    app.config.from_pyfile('settings.cfg', silent=True)
    app.config.from_object(settings_override)

    app.config['pw'] = CryptContext.from_string(
        app.config['CRYPT_INI_CONTENTS'], section='passlib')

    db.init_app(app)

    register_blueprints(app, __name__, __path__)

    for error, func in ERROR_HANDLERS:
        app.errorhandler(error)(func)

    return app
