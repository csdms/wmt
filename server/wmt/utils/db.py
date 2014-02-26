from __future__ import (absolute_import, print_function)

import os
from sys import stderr as STDERR

from .json import load_component


class Error(Exception):
    pass


class LoadError(Error):
    def __init__(self, msg):
        self._msg = msg

    def __str__(self):
        return self._msg


def _component_names(palette_dir):
    names = set()
    for item in os.listdir(palette_dir):
        if os.path.isdir(os.path.join(palette_dir, item)):
            names.add(item)
    return names


def _load_table(name, db='.'):
    import json

    path_to_table = os.path.join(db, name + '.json')
    try:
        with open(path_to_table, 'r') as opened:
            try:
                table = json.loads(opened.read())
            except ValueError as error:
                raise LoadError(str(error))
    except IOError as error:
        table = []

    return table


def _construct_component_from_db(path_to_db):
    desc = _load_table('info', db=path_to_db)

    for table in ['parameters', 'uses', 'provides', 'files']:
        desc[table] = _load_table(table, db=path_to_db)

    return desc


def load_palette(palette_dir):

    palette = {}
    for name in _component_names(palette_dir):
        path_to_db = os.path.join(palette_dir, name, 'db')
        try:
            palette[name] = _construct_component_from_db(path_to_db)
        except LoadError:
            print('%s: unable to load' % name, file=STDERR)

    return palette
