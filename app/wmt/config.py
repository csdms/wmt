from __future__ import print_function

import web
import os
import sys
import logging


def _read_config_file(path_to_file):
    from ConfigParser import RawConfigParser
    from passlib.context import CryptContext

    config = RawConfigParser()
    config.read(path_to_file)

    site = dict(config.items('wmt'))
    site['pw'] = CryptContext.from_path(path_to_file, section='passlib')
    return site


def read_site_config_file(*args):
    import os
    try:
        prefix = args[0]
    except IndexError:
        try:
            prefix = os.environ['WMT_PREFIX']
        except KeyError:
            raise

    os.environ['WMT_PREFIX'] = os.path.abspath(prefix)
    path = os.path.join(os.environ['WMT_PREFIX'], 'conf', 'wmt.ini')
    return _read_config_file(path)


def _load_palette(palette_dir):
    from .utils.json import load_component


    palette = {}
    for file in os.listdir(palette_dir):
        try:
            desc = load_component(os.path.join(palette_dir, file))
        except ValueError:
            logger.error('%s: unable to load component' % os.path.join(palette_dir, file))
        else:
            palette[desc['id']] = desc

    return palette


site = read_site_config_file()
db = web.database(dbn='sqlite', db=site['database'])
palette = _load_palette(os.path.join(site['data'], 'components'))
logger = logging.getLogger('wmtserver')
