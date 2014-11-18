import os
from datetime import datetime

from flask import current_app
from flask.ext.login import current_user

from sqlalchemy import Column, Integer, Text
from ..database import SESSIONS
from ...utils.io import write_readme


Base = SESSIONS['submission']['base']
db_session = SESSIONS['submission']['session']
#STAGE_DIR = '/data/web/htdocs/wmt/api/v1/files/downloads'


class Sim(Base):
    __tablename__ = 'submission'

    id = Column(Integer, primary_key=True)
    model_id = Column(Integer)
    uuid = Column(Text)
    name = Column(Text)
    status = Column(Text)
    created = Column(Text)
    updated = Column(Text)
    message = Column(Text)
    owner = Column(Text)
    stage_dir = Column(Text)

    def __init__(self, name, model_id, owner=None):
        now = datetime.now().isoformat()

        self.model_id = model_id
        self.uuid = str(uuid4())
        self.name = name
        self.status = 'submitted'
        self.message = 'Run has been submitted'
        self.created = now
        self.updated = now
        self.owner = owner or ""
        self.stage_dir = os.path.join(current_app.config['STAGE_DIR'],
                                      self.uuid)

        self._create_stage_dir()

    def __repr__(self):
        return '<Sim %r>' % self.name

    def _create_stage_dir(uuid):
        path = self.stage_dir

        try:
            os.mkdir(path)
        except OSError:
            logger.warning('%s: Stage directory already exists' % path)

        write_readme(path, mode='w', params={
            'user': 'anonymous',
            'staged_on': datetime.now().isoformat()
        })


def add(name, model_id):
    owner = current_user.get_id() or ""

    sim = Sim(name, model_id, owner=owner)

    db_session.add(sim)
    db_session.commit()

    return sim


def all(sort=None, order='asc'):
    if sort is None:
        sims = Sim.query.all()
    else:
        sims = Sim.query.order_by(getattr(Sim, sort))

    if order == 'asc':
        return sims
    else:
        return sims[::-1]


def get(id):
    return Sim.query.get(id)


def remove(sim):
    db_session.delete(sim)
    db_session.commit()


def update_status(id, status=None, message=None):
    sim = get(id)

    if status is not None:
        sim.status = status
    if message is not None:
        sim.message = message
    sim.updated = datetime.now().isoformat()

    db_session.commit()

    return True


def query(**kwds):
    return Sim.query.filter_by(**kwds)
