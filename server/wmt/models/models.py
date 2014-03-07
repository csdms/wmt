import os
import web
import json
from datetime import datetime

from ..config import (site, db)


class Error(Exception):
    pass


class BadIdError(Error):
    def __init__(self, id):
        self._id = id

    def __str__(self):
        return str(self._id)


def new_model(name, text, owner=''):
    id = db.insert('models', name=name, json=text, owner=owner,
                    date=web.net.httpdate(datetime.now()))
    create_model_upload_dir(id)
    return id


def del_model(id):
    db.delete('models', where='id=$id', vars=locals())
    try:
        os.removedirs(get_model_upload_dir(id))
    except os.error as error:
        if error.errno == 2:
            pass
        else:
            raise


def update_model(id, name, text):
    db.update('models', where='id=$id', vars=locals(),
              name=name, json=text, date=web.net.httpdate(datetime.now()))


def get_models():
    return db.select('models', order='id DESC')


def get_model(id):
    try:
        return db.select('models', where='id=$id', vars=locals())[0]
    except IndexError:
        raise BadIdError(id)


def get_model_upload_dir(id):
    return os.path.join(site['uploads'], str(id))


def create_model_upload_dir(id):
    model_upload_dir = get_model_upload_dir(id)
    os.makedirs(model_upload_dir)
