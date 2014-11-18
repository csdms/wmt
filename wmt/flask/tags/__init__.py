from flask import current_app

from ..core import Service, db
from .models import Tag
#from .models import Tag, ModelTag


class TagsService(Service):
    __model__ = Tag

    def create(self, name, owner=0):
        tag = self.first(tag=name, owner=owner)
        if tag is None:
            return super(TagsService, self).create(name, owner)
        else:
            return tag
