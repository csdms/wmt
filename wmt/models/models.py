import os
import web
import json
from datetime import datetime

from .components import get_component
from ..config import (site, db, tag_db)
from ..session import get_username
from .tags import select_public_models


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


    try:
        shutil.rmtree(get_model_upload_dir(id))
    except os.error as error:
        if error.errno == 2:
            pass
        else:
            raise
    db.delete('models', where='id=$id', vars=locals())
    tag_db.delete('model_tags', where='model_id=$id', vars=locals())


def update_model(id, name, text):
    db.update('models', where='id=$id', vars=locals(),
              name=name, json=text, date=web.net.httpdate(datetime.now()))


def get_public_models():
    #return db.select('models', order='id DESC', where='owner=$owner',
    #                 vars=dict(owner=''))
    return set(select_public_models())
    #return tags.select_models_tagged_with('public'):


def get_private_models():
    return db.select('models', order='id DESC', where='owner=$owner',
                     vars=dict(owner=get_username()))


def get_models(sortby=None):
    where = ' OR '.join(['owner=$user', 'owner=\'\''])
    ids = set()
    for entry in db.select('models', order='id DESC', where=where, what='id', vars=dict(user=get_username())):
        ids.add(entry['id'])
    ids |= get_public_models()

    models = []
    for id in ids:
        try:
            models.append(get_model(id))
        except BadIdError:
            pass

    if sortby:
        models.sort(key=lambda model: model[sortby])

    return models


def get_model(id):
    try:
        model = db.select('models', where='id=$id', vars=locals())[0]
    except IndexError:
        raise BadIdError(id)

    return model


def get_model_component(id, component):
    model = json.loads(get_model(id)['json'])
    for item in model['model']:
        if item['id'] == component:
            return item
    raise KeyError(component)


def get_model_yaml(id):
    import yaml

    conf = json.loads(get_model(id)['json'])
    name, model = conf['name'], conf['model']

    components = []
    info = dict()
    for component in model:
        d = {}
        d['name'] = str(component['id'])

        c = get_component(component['id'])

        c_uses = {}
        for port in c['uses']:
            c_uses[port['id']] = port

        try:
            d['class'] = str(c['class'])
        except KeyError:
            d['class'] = str(c['name'])
        d['run_dir'] = str(component['id'])
        d['time_step'] = c.get('time_step', 1.)
        #d['argv'] = [str(component['id'] + '.txt')]
        d['argv'] = [str(arg) for arg in c['argv']]
        d['initialize_args'] = str(c.get('initialize_args', ''))

        d['connectivity'] = []
        for (uses, server) in component['connect'].items():
            if server is not None:
                provides, name = server.split('@')
                d['connectivity'].append({
                    'name': str(uses),
                    'connect': str(name),
                    #'exchange_items': [],
                    'exchange_items': [ str(item) for item in c_uses[str(uses)]['exchange_items'] ],
                })

        params = component['parameters']

        d['print'] = []
        if 'output_interval' in params and 'output_format' in params:
            interval = params['output_interval']
            format = params['output_format']
            for (var, value) in params.items():
                if '__' in var and value != 'off':
                    d['print'].append({
                        'name': str(value),
                        'interval': float(interval),
                        'format': str(format),
                    })

        if component.get('driver', False):
            info['driver'] = str(component['id'])
            info['duration'] = float(params['run_duration'])

        components.append(d)

    #return yaml.dump_all([info] + components, default_flow_style=False)
    return (yaml.dump(info, default_flow_style=False),
            yaml.dump_all(components, default_flow_style=False))


def get_model_ids():
    entries = db.select('models', what='id')
    return [str(entry['id']) for entry in entries]


def get_model_upload_dir(id):
    return os.path.join(site['uploads'], str(id))


def create_model_upload_dir(id):
    model_upload_dir = get_model_upload_dir(id)
    os.makedirs(model_upload_dir)
