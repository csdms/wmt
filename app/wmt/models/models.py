import os
import web
import json

from ..config import (site, db)


def new_model(name, text, owner=''):
    return db.insert('models', name=name, json=text, owner=owner)


def del_model(id):
    db.delete('models', where='id=$id', vars=locals())


def update_model(id, name, text):
    db.update('models', where='id=$id', vars=locals(),
              name=name, json=text)


def get_models():
    return db.select('models', order='id DESC')


def get_model(id):
    try:
        return db.select('models', where='id=$id', vars=locals())[0]
    except IndexError:
        return None
