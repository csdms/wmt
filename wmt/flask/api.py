from flask import Flask

from .core import db
from .blueprints import register_blueprints


#def create_app(package_name, package_path, settings_override=None,
#               register_security_blueprint=True):
def create_app(settings_override=None, register_security_blueprint=True):
    app = Flask(__name__, instance_relative_config=True)

    app.config.from_object('wmt.flask.settings')
    app.config.from_pyfile('settings.cfg', silent=True)
    app.config.from_object(settings_override)

    db.init_app(app)

    register_blueprints(app, __name__, __path__)

    return app
