from flask import current_app

from ..core import Service, db
from .models import Sim


class SimsService(Service):
    __model__ = Sim
