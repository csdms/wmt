from __future__ import print_function

import web
import os
import sys
import logging

from .utils.db import load_palette


def _read_config_file(path_to_file, prefix=None):
    from ConfigParser import ConfigParser
    from passlib.context import CryptContext

    config = ConfigParser()
    config.read(path_to_file)

    if prefix is None:
        prefix = os.path.abspath(
            os.path.join(os.path.dirname(path_to_file), '..'))

    site = dict(config.items('paths'))
    for (name, path) in site.items():
        site[name] = os.path.join(prefix, path)
    site.update(config.items('url'))
    for (key, value) in config.items('pickup'):
        site['pickup_' + key] = value

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
if not os.path.isfile(site['database']):
    raise ValueError(site['database'])
submission_db = web.database(dbn='sqlite', db=site['submission_db'])
user_db = web.database(dbn='sqlite', db=site['user_db'])
tag_db = web.database(dbn='sqlite', db=os.path.join(site['db'], 'tag.db'))
names_db = web.database(dbn='sqlite', db=site['names_db'])
palette = load_palette(os.path.join(site['db'], 'components'))

logging.basicConfig()
logger = logging.getLogger('wmtserver')
