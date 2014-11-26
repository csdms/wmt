from datetime import datetime
import json

from flask import url_for

from ..core import db, JsonMixin


class ModelJsonSerializer(JsonMixin):
    __public_fields__ = set(['href', 'id', 'date', 'name'])


class Model(ModelJsonSerializer, db.Model):
    __tablename__ = 'models'
    __bind_key__ = 'tags'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Text)
    date = db.Column(db.Text)
    json = db.Column(db.Text)
    owner = db.Column(db.String(128), db.ForeignKey('users.username'))

    tags = db.relationship('Tag', secondary='model_tags', backref='model_tags',
                           lazy='dynamic')
    sims = db.relationship('Sim', backref='model')

    @property
    def href(self):
        return url_for('.model', id=self.id)

    @property
    def object_links(self):
        links = []
        for tag in self.tags:
            links.append(dict(rel='resource/tags',
                              href=url_for('tags.tag', id=tag.id)))
        return links

    @property
    def link_objects(self):
        return {
            'user': { 'href': url_for('users.user', id=self.owned_by.id)},
        }

    def __init__(self, name, json, owner=None):
        self.name = name
        self.json = json
        self.date = datetime.now().isoformat()
        self.owner = owner or ""

    def __repr__(self):
        return '<Model %r>' % self.name

    def components(self):
        return [c['id'] for c in json.loads(self.json)['model']]
