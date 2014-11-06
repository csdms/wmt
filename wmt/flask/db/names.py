from flask.ext.login import current_user

from sqlalchemy import Column, Integer, Text
from ..database import SESSIONS


Base = SESSIONS['names']['base']
db_session = SESSIONS['names']['session']


class Name(Base):
    __tablename__ = 'names'

    id = Column(Integer, primary_key=True)
    name = Column(Text)

    def __init__(self, name):
        self.name = name

    def __repr__(self):
        return '<Name %r>' % self.name


def all(sort=None, order=None):
    if sort is None:
        names = Name.query.all()
    else:
        names = Name.query.order_by(getattr(Name, sort))

    if order == 'asc':
        return names
    else:
        return names[::-1]


def get_by_id(id):
    return Name.query.get(id)


def contains(needle):
    return Name.query.filter(Name.name.like('%' + needle + '%'))
