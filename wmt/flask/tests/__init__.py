import json
import tempfile
import shutil

from wmt.flask import create_app
from wmt.flask.settings import WmtDebugSettings
from wmt.flask.core import db
from wmt.flask.models.models import Model
from wmt.flask.users.models import User
from wmt.flask.tags.models import Tag
from wmt.flask.sims.models import Sim


#WMT_DATABASE_DIR = tempfile.mkdtemp(prefix='wmt-testing-db', suffix='.d')
WMT_ROOT_DIR = tempfile.mkdtemp(prefix='wmt-testing', suffix='.d')

app = create_app(settings_override=WmtDebugSettings(WMT_ROOT_DIR))


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
FAKE_SIM_NAME = u'foobar'
FAKE_SIM_MODEL = 1
FAKE_SIM = {
    'name': FAKE_SIM_NAME,
    'model': FAKE_SIM_MODEL,
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
        db.session.add(Tag(FAKE_TAG['tag'], 1))
        db.session.add(Sim(FAKE_SIM_NAME, FAKE_SIM_MODEL))
        try:
            db.session.commit()
        except:
            pass


def teardown():
    shutil.rmtree(WMT_ROOT_DIR)
