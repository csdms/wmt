from flask import url_for

from standard_names import StandardName
from ..core import db, JsonMixin


class NameJsonSerializer(JsonMixin):
    __public_fields__ = set(['href', 'id', 'name', 'object', 'quantity',
                             'operators'])


class Name(NameJsonSerializer, db.Model):
    __tablename__ = 'names'
    __bind_key__ = 'names'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Text)

    @property
    def href(self):
        return url_for('names.name', id=self.id)

    @property
    def object(self):
        return StandardName(self.name).object

    @property
    def quantity(self):
        return StandardName(self.name).quantity

    @property
    def operators(self):
        return StandardName(self.name).operators

    def __init__(self, name):
        self.name = name

    def __repr__(self):
        return '<Name %r>' % self.name
