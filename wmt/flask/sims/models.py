import os
from datetime import datetime
from uuid import uuid4
import json
from distutils.dir_util import mkpath

from flask import current_app, url_for

from ..core import db, JsonMixin


class SimJsonSerializer(JsonMixin):
    __hidden_fields__ = set(['stage_dir'])
    __public_fields__ = set(['id', 'name', 'status', 'owner', 'message',
                             'href'])


class Sim(SimJsonSerializer, db.Model):
    __tablename__ = 'submission'
    __bind_key__ = 'sims'

    id = db.Column(db.Integer, primary_key=True)
    model_id = db.Column(db.Integer, db.ForeignKey('models.id'))
    uuid = db.Column(db.Text)
    name = db.Column(db.Text)
    status = db.Column(db.Text)
    created = db.Column(db.Text)
    updated = db.Column(db.Text)
    message = db.Column(db.Text)
    owner = db.Column(db.Integer, db.ForeignKey('users.id'))
    stage_dir = db.Column(db.Text)

    @property
    def href(self):
        return url_for('sims.sim', id=self.id)

    @property
    def link_objects(self):
        return {
            'user': {'href': url_for('users.user', id=self.owner)},
            'model': {'href': url_for('models.model', id=self.model_id)},
        }

    def __init__(self, name, model_id, owner=None):
        now = datetime.now().isoformat()

        self.model_id = model_id
        self.uuid = str(uuid4())
        self.name = name
        self.status = 'submitted'
        self.message = 'Run has been submitted'
        self.created = now
        self.updated = now
        self.owner = owner or 0
        self.stage_dir = os.path.join(current_app.config['STAGE_DIR'],
                                      self.uuid)

    def __repr__(self):
        return '<Sim %r>' % self.name

    def create_stage_dir(self):
        wmt_dir = os.path.join(self.stage_dir, '.wmt')
        mkpath(wmt_dir)

        with open(os.path.join(wmt_dir, 'sim.json'), 'w') as fp:
            fp.write(json.dumps({'uuid': self.uuid,
                                 'name': self.name,
                                 'created': self.created,
                                 'updated': self.updated,
                                 'owner': self.owner,
                                 'model': self.model.id,
                                 'model_name': self.model.name,
                                 'user': self.user.username
                                }))

        with open(os.path.join(wmt_dir, 'model.json'), 'w') as fp:
            fp.write(self.model.json)

    def update_status(self, sim, status=None, message=None):
        updates = dict(updated=datetime.now().isoformat())
        if status is not None:
            updates['status'] = status
        if message is not None:
            updates['message'] = status

        self.update(sim, **updates)

        return sim
