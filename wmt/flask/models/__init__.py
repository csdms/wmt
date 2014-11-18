from flask import current_app

from ..core import Service, db
from .models import Model


class ModelsService(Service):
    __model__ = Model

    def create(self, name, json, owner=None):
        model = self.first(name=name, owner=owner)
        if model is None:
            return super(ModelsService, self).create(name, json, owner=owner)
        else:
            return model
