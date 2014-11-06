from flask.ext.login import current_user

from sqlalchemy import Column, Integer, String
from ..database import SESSIONS

Base = SESSIONS['tag']['base']
db_session = SESSIONS['tag']['session']


class Tag(Base):
    __tablename__ = 'tags'

    id = Column(Integer, primary_key=True)
    tag = Column(String(128))
    owner = Column(String(128))

    def __init__(self, tag, owner=None):
        self.tag = tag
        self.owner = owner or ''

    def __repr__(self):
        return '<Tag %r>' % self.tag


class ModelTag(Base):
    __tablename__ = 'model_tags'

    model_id = Column(Integer, primary_key=True)
    tag_id = Column(Integer)

    def __init__(self, model, tag):
        self.model = model
        self.tag = tag

    def __repr__(self):
        return '<ModelTag %r, %r>' % (self.model, self.tag)


def all():
    return Tag.query.all()


def get_by_id(id):
    return Tag.query.filter_by(id=id).first()


def add(name, owner=None):
    owner = owner or (current_user.get_id() or "")
    tag = Tag.query.filter_by(tag=name, owner=owner).first()

    if tag is None:
        tag = Tag(name, owner=owner)
        db_session.add(tag)
        db_session.commit()

    return tag


def remove(tag):
    db_session.delete(tag)
    db_session.commit()


def query(**kwds):
    return Tag.query.filter_by(**kwds)


def models_with_tag(id):
    return ModelTag.query.filter_by(tag_id=id)


def tags_with_model(id):
    entries = ModelTag.query.filter_by(model_id=id)
    return [get_by_id(entry.tag_id) for entry in entries]
    #if entries:
    #else:
    #    return []
