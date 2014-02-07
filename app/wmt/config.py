import web
import yaml


#from .paths import PREFIX

def wmt_conf(**kwds):
    prefix = kwds.default('prefix', PREFIX)
    database_dir = kwds.default('database_dir', os.path.join(prefix, 'db'))

    from string import Template
    contents = Template("""
wmt:
    database_dir: ${prefix}/db
    data_dir: ${prefix}/data
    """)
    return contents.substitute(prefix=prefix)


def load_wmt_conf(file):
    with open(file, 'r') as conf_file:
        conf = yaml.load(conf_file.read())
    return conf


def read_site_conf(site_prefix):
    from ConfigParser import RawConfigParser

    conf_file_path = os.path.join(site_prefix, 'conf', 'cmt.ini')
    conf = RawConfigParser()

    #with open(conf_file_path, 'r') as conf_file:
    #    conf = yaml.load(conf_file.read())

    return conf


def read_config_file(path_to_file):
    from ConfigParser import RawConfigParser
    from passlib.context import CryptContext

    config = RawConfigParser()
    config.read(path_to_file)

    site = dict(config.items('cmt'))
    site['pw'] = CryptContext.from_path(path_to_file, section='passlib')
    return site


def write_config_file(path_to_file, items):
    from ConfigParser import RawConfigParser
    config = RawConfigParser()
    for item in items:
        conf.set('cmt', *items)
    config.write(path_to_file)


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
    path = os.path.join(os.environ['WMT_PREFIX'], 'conf', 'cmt.ini')
    return read_config_file(path)


site = read_site_config_file()
db = web.database(dbn='sqlite', db=site['database'])
