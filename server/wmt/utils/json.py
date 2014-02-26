from __future__ import absolute_import

import json


def load_mapping(filename):
    with open(filename, 'r') as map:
        mapping = json.loads(map.read())

    if not isinstance(mapping, dict):
        raise ValueError(filename)

    return mapping


def load_component(filename):
    with open(filename, 'r') as file:
        contents = file.read()

    try:
        desc = json.loads(contents)
    except ValueError:
        raise
    else:
        if desc.has_key('parameters'):
            return desc
        else:
            raise ValueError(filename)


def load_component_defaults(filename):
    defaults = {}
    desc = load_component(filename)
    for parameter in desc['parameters']:
        defaults[parameter['key']] = parameter['value']['default']

    return defaults
