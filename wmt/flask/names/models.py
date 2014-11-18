#from flask_security import UserMixin, RoleMixin
from standard_names import StandardName
from ..core import db


class Name(db.Model):
    __tablename__ = 'names'
    __bind_key__ = 'names'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Text)

    def __init__(self, name):
        self.name = name

    def __repr__(self):
        return '<Name %r>' % self.name

    def to_resource(self, brief=False):
        if brief:
            return {'id': self.id, 'name': self.name}
        else:
            sn = StandardName(self.name)
            return {
                'id': self.id,
                'href': '/api/names/%d' % self.id,
                'name': self.name,
                'object': sn.object,
                'quantity': sn.quantity,
                'operators': sn.operators,
            }
