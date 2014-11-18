from datetime import datetime
import json

from flask import url_for

from ..core import db, JsonMixin


class Model(db.Model, JsonMixin):
    __tablename__ = 'models'
    __bind_key__ = 'tags'
    #__bind_key__ = 'models'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Text)
    date = db.Column(db.Text)
    json = db.Column(db.Text)
    owner = db.Column(db.String(128), db.ForeignKey('users.username'))

    tags = db.relationship('Tag', secondary='model_tags', backref='model_tags')

    def __init__(self, name, json, owner=None):
        self.name = name
        self.json = json
        self.date = datetime.now().isoformat()
        self.owner = owner or ""

    def __repr__(self):
        return '<Model %r>' % self.name

    def components(self):
        return [c['id'] for c in json.loads(self.json)['model']]

    def to_resource(self):
        links = [{
            'rel': 'resource/blueprint',
            'href': url_for('models.blueprint', id=self.id),
        }]
        for tag in self.tags:
            link = dict(rel='collection/tags')
            if tag is not None:
                link['href'] = url_for('tags.tag', id=tag.id)
            else:
                link['href'] = None
            links.append(link)
        for name in self.components():
            links.append({
                'rel': 'resource/component',
                'href': url_for('components.component', name=name),
            })
        return {
            '_type': 'model',
            'id': self.id,
            'href': '/api/models/%d' % self.id,
            'date': self.date,
            'owner': self.owner or None,
            'name': self.name,
            'links': links,
        }
