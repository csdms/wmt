from sqlalchemy import Column, Integer, Text

from passlib.context import CryptContext


from ..database import SESSIONS
from ..utils import as_resource, as_collection


Base = SESSIONS['users']['base']
db_session = SESSIONS['users']['session']

pw = CryptContext.from_path('/data/web/htdocs/wmt/api/v1/conf/wmt.ini',
                            section='passlib')


class Username(Base):
    __tablename__ = 'users'

    id = Column(Integer, primary_key=True)
    username = Column(Text, unique=True)
    password = Column(Text)

    def __init__(self, username, password):
        self.username = username
        self.password = password

    def __repr__(self):
        return '<User %r>' % self.username


def add(name, password):
    user = get_by_name(name)

    if user is None:
        db_session.add(Username(name, pw.encrypt(password)))
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
        user.password = pw.encrypt(new)
        db_session.commit()
        return True
    return False
