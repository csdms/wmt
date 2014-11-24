import datetime

from ..core import db, JsonMixin


class SimJsonSerializer(JsonMixin):
    __hidden_fileds__ = set(['stage_dir'])


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
    def url_for(self):
        return url_for('.sim', id=self.id)

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

    def update_status(self, sim, status=None, message=None):
        updates = dict(updated=datetime.now().isoformat())
        if status is not None:
            updates['status'] = status
        if message is not None:
            updates['message'] = status

        self.update(sim, **updates)

        return sim
