from __future__ import absolute_import

import os
import json

from .rc_file import ResourceFile


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


def load_component_defaults(file):
    defaults = {}
    desc = load_component_from_json(file)
    for parameter in desc['parameters']:
        defaults[parameter['key']] = parameter['value']['default']

    return defaults


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
