import sqlite3

from flask import Flask, g, session, request, redirect, url_for
from flask_login import LoginManager
#from flaskext.uploads import UploadSet, All, configure_uploads

from passlib.context import CryptContext

from .database import start_engines


DEBUG = True
SECRET_KEY = 'super secret key'
SERVER_NAME = 'csdms.colorado.edu'
UPLOADS_DEFAULT_DEST = '/data/web/htdocs/wmt/api/dev/files/uploads'
#UPLOADED_DATAFILES_DEST = '/data/web/htdocs/wmt/api/dev/files/uploads'
UPLOAD_DIR = '/data/web/htdocs/wmt/api/dev/files/uploads'
STAGE_DIR = '/data/web/htdocs/wmt/api/dev/files/downloads'
DATABASE_DIR = '/data/web/htdocs/wmt/api/v1/db'
CRYPT_INI_PATH = '/data/web/htdocs/wmt/api/v1/conf/wmt.ini'
CRYPT_INI = """
[passlib]
schemes = sha512_crypt, sha256_crypt
sha256_crypt__default_rounds = 100000
sha512_crypt__default_rounds = 100000
""".strip()


app = Flask(__name__)
app.config.from_object(__name__)

login_manager = LoginManager()
login_manager.init_app(app)

app.config['pw'] = CryptContext.from_string(app.config['CRYPT_INI'],
                                            section='passlib')


start_engines()


from .views import (names_page, tags_page, users_page, models_page,
                    components_page, sims_page)
from .utils import as_resource, as_collection

PAGES = [
    (names_page, 'names'),
    (tags_page, 'tags'),
    (users_page, 'users'),
    (models_page, 'models'),
    (components_page, 'components'),
    (sims_page, 'sims'),
]
for page in PAGES:
    app.register_blueprint(page[0], url_prefix='/' + page[1])


#files = UploadSet('datafiles', extensions=All)
#app.config['files'] = files
#app.config['UPLOADED_DATAFILES_DEST'] = '/data/web/htdocs/wmt/api/dev/files/uploads'
#configure_uploads(app, (files, ))


class User(object):
    def __init__(self, id):
        self._id = str(id)

    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    def get_id(self):
        return self._id


@login_manager.user_loader
def load_user(userid):
    return User(userid)


def site_map():
    routes = []

    routes.append(url_for('models.show', _external=True))
    routes.append(url_for('names.show', _external=True))
    routes.append(url_for('tags.show', _external=True))
    routes.append(url_for('users.show', _external=True))
    routes.append(url_for('components.show', _external=True))
    routes.append(url_for('users.user', id=0, _external=True))
    routes.append(url_for('sims.show', id=0, _external=True))

    return routes


@app.route('/')
def index():
    if request.authorization is not None:
        redirect(url_for('login'))
        return as_resource(session['username'])
    else:
        return as_collection(site_map())


#def connect_db():
#    return sqlite3.connect(current_app.config['USER_DATABASE'])


#def get_db():
#    db = getattr(g, '_user_database', None)
#    if db is None:
#        db = g._user_database = connect_db()
#    db.row_factory = make_dicts
#    return db


#def make_dicts(cur, row):
#    return dict((cur.description[idx][0], value)
#                for idx, value in enumerate(row))


#def query_db(query, args=(), one=False):
#    cur = get_db().execute(query, args)
#    rv = cur.fetchall()
#    cur.close()
#    return (rv[0] if rv else None) if one else rv


#def authenticate_user(username, password):
#    user = query_db('select * from users where username = ?',
#                    [username], one=True)
#    return user is not None and pw.verify(password, user['password'])


#@app.teardown_appcontext
#def close_connection(exception):
#    db = getattr(g, '_database', None)
#    if db is not None:
#        db.close()

#@app.teardown_appcontext
#def shutdown_session(exception=None):
#    db_session.remove()


if __name__ == '__main__':
    app.run()
