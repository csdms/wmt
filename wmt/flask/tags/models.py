import json

from flask import url_for
from sqlalchemy.ext.declarative import declarative_base

from ..core import db, JsonMixin


model_tags = db.Table(
    'model_tags', db.metadata,
    db.Column('model_id', db.Integer, db.ForeignKey('models.id')),
    db.Column('tag_id', db.Integer, db.ForeignKey('tags.id')),
    info={'bind_key': 'tags'})


class Tag(db.Model, JsonMixin):
    __tablename__ = 'tags'
    __bind_key__ = 'tags'

    id = db.Column(db.Integer, primary_key=True)
    tag = db.Column(db.String(128))
    #owner = db.Column(db.String(128), db.ForeignKey('users.username'))
    owner = db.Column(db.Integer, db.ForeignKey('users.id'))

    models = db.relationship('Model', secondary=model_tags,
                             backref='tag_models')

    def __init__(self, tag, owner=0):
        self.tag = tag
        self.owner = owner

    def __repr__(self):
        return '<Tag %r>' % self.tag

    def models_with_tag(self, tag):
        return self.models.find(tag_id=tag.id)
        #return ModelTag.query.filter_by(tag_id=id)

    def to_resource(self):
        links = []
        #for model in self.models.find(tag_id=self.id):
        for model in self.models:
            links.append(model.id)
        try:
            #user_id = users.first(username=self.owner).id
            user_id = self.owned_by.id
        except AttributeError:
            user_id = 0
        return {
            '_type': 'tag',
            'id': self.id,
            'href': url_for('tags.tag', id=self.id),
            'tag': self.tag,
            #'owner': url_for('users.user', id=self.owner),
            'user': {
                'href': url_for('users.user', id=user_id),
            },
        }
