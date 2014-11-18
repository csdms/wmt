from flask import current_app

from ..core import Service, db
from .models import Name


class NamesService(Service):
    __model__ = Name
