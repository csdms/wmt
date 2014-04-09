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


class AuthorizationError(Error):
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
    import shutil


    db.delete('models', where='id=$id', vars=locals())
    try:
        shutil.rmtree(get_model_upload_dir(id))
    except os.error as error:
        if error.errno == 2:
            pass
        else:
            raise


def update_model(id, name, text):
    db.update('models', where='id=$id', vars=locals(),
              name=name, json=text, date=web.net.httpdate(datetime.now()))


def get_public_models():
    return db.select('models', order='id DESC', where='owner=$owner', vars=dict(owner=''))


def get_private_models():
    return db.select('models', order='id DESC', where='owner=$owner', vars=dict(owner=web.ctx.session.username))


def get_model(id):
    try:
        model = db.select('models', where='id=$id', vars=locals())[0]
    except IndexError:
        raise BadIdError(id)

    if model['owner'] in ['', web.ctx.session.username]:
        return model
    else:
        raise AuthorizationError(id)


def get_model_component(id, component):
    model = json.loads(get_model(id)['json'])
    for item in model['model']:
        if item['id'] == component:
            return item
    raise KeyError(component)


def get_model_ids():
    entries = db.select('models', what='id')
    return [str(entry['id']) for entry in entries]


def get_model_upload_dir(id):
    return os.path.join(site['uploads'], str(id))


def create_model_upload_dir(id):
    model_upload_dir = get_model_upload_dir(id)
    os.makedirs(model_upload_dir)
