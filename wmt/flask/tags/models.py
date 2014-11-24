import json

from flask import url_for
from sqlalchemy.ext.declarative import declarative_base

from ..core import db, JsonMixin


model_tags = db.Table(
    'model_tags', db.metadata,
    db.Column('model_id', db.Integer, db.ForeignKey('models.id')),
    db.Column('tag_id', db.Integer, db.ForeignKey('tags.id')),
    info={'bind_key': 'tags'})


class TagJsonSerializer(JsonMixin):
    __public_fields__ = set(['href', 'id', 'tag'])


class Tag(TagJsonSerializer, db.Model):
    __tablename__ = 'tags'
    __bind_key__ = 'tags'

    id = db.Column(db.Integer, primary_key=True)
    tag = db.Column(db.String(128))
    owner = db.Column(db.Integer, db.ForeignKey('users.id'))

    models = db.relationship('Model', secondary=model_tags,
                             backref='tag_models', lazy='dynamic')

    @property
    def href(self):
        return url_for('tags.tag', id=self.id)

    @property
    def object_links(self):
        links = []
        for model in self.models:
            links.append(dict(rel='resource/models',
                              href=url_for('models.model', id=model.id)))
        return links

    @property
    def link_objects(self):
        return {
            'user': { 'href': url_for('users.user', id=self.owned_by.id)},
        }

    def __init__(self, tag, owner=0):
        self.tag = tag
        self.owner = owner

    def __repr__(self):
        return '<Tag %r>' % self.tag

    def models_with_tag(self):
        return self.models.find(tag_id=tag.id)
