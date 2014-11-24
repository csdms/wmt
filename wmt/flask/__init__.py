import os

from flask import Flask, url_for, jsonify
from flask_login import LoginManager

from passlib.context import CryptContext

from .settings import WmtSettings
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


def create_app(settings_override=None, register_security_blueprint=True,
               wmt_root_path=None):
    app = Flask(__name__, instance_relative_config=True)

    login_manager = LoginManager()
    login_manager.init_app(app)

    @login_manager.user_loader
    def load_user(userid):
        return User(userid)

    @app.before_first_request
    def create_database():
        db.create_all()

    app.config.from_object(WmtSettings(wmt_root_path or app.root_path))
    app.config.from_pyfile('settings.cfg', silent=True)
    app.config.from_object(settings_override)

    app.config['pw'] = CryptContext.from_string(
        app.config['CRYPT_INI_CONTENTS'], section='passlib')

    import logging
    logging.basicConfig()
    app.config['log'] = logging.getLogger('wmtserver')

    db.init_app(app)

    @app.route('/')
    def site_map():
        COLLECTIONS = ['users', 'names', 'components', 'models', 'tags',
                       'sims']
        map = {"@type": "api", "href": url_for('.site_map')}
        links = []
        for rel in COLLECTIONS:
            href = url_for('.'.join([rel, 'show']))
            links.append({'rel': rel, 'href': href})
        map['links'] = links
        return jsonify(map)

    register_blueprints(app, __name__, __path__)

    for error, func in ERROR_HANDLERS:
        app.errorhandler(error)(func)

    return app
