from datetime import datetime

from flask.ext.login import current_user

from sqlalchemy import Column, Integer, Text
from ..database import SESSIONS

Base = SESSIONS['wmt']['base']
db_session = SESSIONS['wmt']['session']


class Model(Base):
    __tablename__ = 'models'

    id = Column(Integer, primary_key=True)
    name = Column(Text)
    date = Column(Text)
    json = Column(Text)
    owner = Column(Text)

    def __init__(self, name, json, owner=None):
        self.name = name
        self.json = json
        self.date = datetime.now().isoformat()
        self.owner = owner or ""

    def __repr__(self):
        return '<Model %r>' % self.name


def all():
    return Model.query.all()


def get(id):
    return Model.query.get(id)


def add(name, json):
    owner = current_user.get_id() or ""
    model = Model.query.filter_by(name=name, owner=owner).first()

    if model is None:
        model = Model(name, json, owner=owner)
        db_session.add(model)
        db_session.commit()

    return model


def update(id, **kwds):
    model = get(id)
    for (key, value) in kwds.items():
        if value is not None:
            setattr(model, key, value)
    model.date = datetime.now().isoformat()
    db_session.commit()

    return model


def remove(model):
    db_session.delete(model)
    db_session.commit()


def query(**kwds):
    return Model.query.filter_by(**kwds)
