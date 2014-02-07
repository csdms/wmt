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
        return model.has_key('arena')
    except AttributeError:
        raise
        #return False


def load_arena_from_json(text):
    model = json.loads(text)
    try:
        return model['arena']
    except KeyError:
        raise ValueError('JSON does not describe a CMT arena')


def components_in_arena(arena):
    names = []
    for component in arena:
        names.append(component['id'])
    return names


def component_parameters(arena, name):
    for component in arena:
        if component['id'] == name:
            return component['parameters'].items()
    raise ValueError(name)


def component_connections(arena, name):
    for component in arena:
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
        if desc.has_key('component_parameters'):
            return desc
        else:
            raise ValueError(file)


def rc_from_json(text):
    try:
        arena = load_arena_from_json(text)
    except ValueError:
        raise
    else:
        components = components_in_arena(arena)

    rc = ResourceFile()
    for component in components:
        rc.append('instantiate %s %s' % (component, component))
        for (key, value) in component_parameters(arena, component):
            rc.append('parameters %s Configure %s %s' %
                      (component, key, value))

        for (key, value) in component_connections(arena, component):
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
