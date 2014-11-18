from flask import Response, url_for

from ..core import db, JsonMixin


class User(db.Model, JsonMixin):
    __tablename__ = 'users'
    __bind_key__ = 'users'

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.Text, unique=True)
    password = db.Column(db.Text)
    tags = db.relationship('Tag', backref='owned_by', lazy='dynamic')
    models = db.relationship('Model', backref='owned_by', lazy='dynamic')

    def __init__(self, username, password):
        self.username = username
        self.password = password

    def __repr__(self):
        return '<User %r>' % self.username

    def to_resource(self):
        links = []
        for tag in self.tags:
            links.append(dict(rel='resource/tags',
                              href=url_for('tags.tag', id=tag.id)))
        for model in self.models:
            links.append(dict(rel='resource/models',
                              href=url_for('models.model', id=model.id)))
        resource = {
            '_type': 'user',
            'id': self.id,
            'href': url_for('.user', id=self.id),
            'username': self.username,
            'links': [
                {'rel': 'collection/tags',
                 'href': url_for('.tags', id=self.id)
                },
                {'rel': 'collection/models',
                 'href': url_for('.models', id=self.id)
                },
            ]
        }
        if len(links) > 0:
            resource['links'] = links
        return resource
