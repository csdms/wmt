from __future__ import print_function

import web
import os
import sys
import logging

from .utils.db import load_palette


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


site = read_site_config_file()
db = web.database(dbn='sqlite', db=site['database'])
submission_db = web.database(dbn='sqlite', db=site['submission_db'])
palette = load_palette(os.path.join(site['db'], 'components'))
logger = logging.getLogger('wmtserver')
