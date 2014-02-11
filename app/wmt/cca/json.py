from __future__ import absolute_import

import os
import json

from .rc_file import ResourceFile

from ..config import site


def check_json_is_valid(text):
    try:
        model = json.loads(text)
    except ValueError:
        raise
        #return False

    try:
        return model.has_key('model')
    except AttributeError:
        raise
        #return False


def load_model_from_json(text):
    model = json.loads(text)
    try:
        return model['model']
    except KeyError:
        raise ValueError('JSON does not describe a CMT model')


def components_in_model(model):
    names = []
    for component in model:
        names.append(component['id'])
    return names


def component_parameters(model, name):
    for component in model:
        if component['id'] == name:
            return component['parameters'].items()
    raise ValueError(name)


def component_connections(model, name):
    for component in model:
        if component['id'] == name:
            try:
                return component['connect'].items()
            except KeyError:
                return []

    raise ValueError(name)


def load_component_from_json(file):
    try:
        desc = json.loads(open(file, 'r').read())
    except ValueError:
        raise
    else:
        if desc.has_key('parameters'):
            return desc
        else:
            raise ValueError(file)


def rc_from_json(text):
    try:
        model = load_model_from_json(text)
    except ValueError:
        raise
    else:
        components = components_in_model(model)

    rc = ResourceFile()
    for component in components:
        rc.append('instantiate %s %s' % (component, component))
        for (key, value) in component_parameters(model, component):
            rc.append('parameters %s Configure %s %s' %
                      (component, key, value))

        for (key, value) in component_connections(model, component):
            (provides_port, provides_component) = value.split('@')
            rc.append('connect %s %s %s %s' %
                      (component, key, provides_component, provides_port))

    return rc.as_string()


def get_palette():
    palette = []
    for file in os.listdir(site['data']):
        try:
            desc = load_component_from_json(os.path.join(site['DATA'], file))
        except ValueError:
            pass
        else:
            palette.append(desc['id'])

    return palette


def get_component(name):
    json_file = os.path.join(site['DATA'], name + '.json')
    try:
        return json.loads(open(json_file, 'r').read())
    except IOError:
        raise
