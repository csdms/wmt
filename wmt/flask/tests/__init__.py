import json

from wmt.flask import create_app
from wmt.flask.core import db
from wmt.flask.models.models import Model
from wmt.flask.users.models import User
from wmt.flask.tags.models import Tag


#app = create_app().test_client()

DEBUG = True
SECRET_KEY = 'super secret key'
CRYPT_INI_CONTENTS = """
[passlib]
schemes = sha512_crypt, sha256_crypt
sha256_crypt__default_rounds = 100000
sha512_crypt__default_rounds = 100000
""".strip()
DATABASE_DIR = '/Users/huttone/git/wmt/db'

SQLALCHEMY_MIGRATE_REPO = path.join(DATABASE_DIR, 'db_repository')
SQLALCHEMY_DATABASE_URI = 'sqlite:///' + path.join(DATABASE_DIR, 'wmt.db')
SQLALCHEMY_BINDS = {
    'names': 'sqlite:///' + path.join(DATABASE_DIR, 'names.db'),
    'tags': 'sqlite:///' + path.join(DATABASE_DIR, 'tag.db'),
    'users': 'sqlite:///' + path.join(DATABASE_DIR, 'users.db'),
    'sims': 'sqlite:///' + path.join(DATABASE_DIR, 'submission.db'),
    'models': 'sqlite:///' + path.join(DATABASE_DIR, 'models.db'),
}

app = create_app()


FAKE_TAG = {
    'tag': u'foobar',
}
FAKE_BLUEPRINT = {
    'model': [dict(id=0), dict(id=1)]
}
FAKE_MODEL = {
    'name': u'foo',
    'json': json.dumps(FAKE_BLUEPRINT),
}
FAKE_USER_NAME = u'foo@bar.baz.edu'
FAKE_USER_PASS = u'foobar'
FAKE_USER = {
    'username': FAKE_USER_NAME,
    'password': FAKE_USER_PASS,
}
FAKE_USER1_NAME = u'bill@microsoft.com'
FAKE_USER1_PASS = u'melinda'


def setup():
    db.init_app(app)
    with app.app_context():
        db.create_all()
        db.session.add(User(FAKE_USER['username'],
                            app.config['pw'].encrypt(FAKE_USER['password'])))
        db.session.add(User(FAKE_USER1_NAME,
                            app.config['pw'].encrypt(FAKE_USER1_PASS)))
        db.session.add(Model(FAKE_MODEL['name'], FAKE_MODEL['json'],
                             FAKE_USER['username']))
        db.session.add(Tag(FAKE_TAG['tag'], 1)) #FAKE_USER['username']))
        try:
            db.session.commit()
        except:
            pass
