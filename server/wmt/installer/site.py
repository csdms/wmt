#! /usr/bin/env python
import os
import shutil
from string import Template
from collections import OrderedDict

import wmt

from .components import download_and_unpack_from_github


_PACKAGE_PREFIX = os.path.abspath(os.path.dirname(wmt.__file__))


def create_empty_database(path_to_db, clobber=True, schema=None):
    import sqlite3
    from contextlib import closing

    if clobber and os.path.isfile(path_to_db):
        try:
            os.remove(path_to_db)
        except OSError:
            print 'unable to remove old database'
            return

    try:
        conn = sqlite3.connect(path_to_db)
    except sqlite3.OperationalError:
        print '%s: Unable to connect to database' % path_to_db
        raise

    if schema is not None:
        with closing(conn) as db:
            with open(schema, 'r') as f:
                db.cursor().executescript(f.read())
            db.commit()

    conn.close()


def copy_dir_contents(src, dest):
    import shutil
    for file in os.listdir(src):
        try:
            shutil.copyfile(os.path.join(src, file),
                            os.path.join(dest, file))
        except IOError:
            pass


def make_directory_tree(prefix, folders):
    try:
        os.mkdir(prefix)
    except OSError:
        pass

    for subdir in folders:
        try:
            os.mkdir(os.path.join(prefix, subdir))
        except OSError:
            pass


def chown_folder_and_files(prefix, user, group):
    import os
    import pwd, grp

    uid, gid = (pwd.getpwnam(user).pw_uid,
                grp.getgrnam(group).gr_gid)

    skipped = []

    try:
        os.chown(prefix, uid, gid)
    except OSError:
        skipped.append(prefix)

    for file in os.listdir(prefix):
        path = os.path.join(prefix, file)
        try:
            os.chown(path, uid, gid)
        except OSError:
            skipped.append(path)

    if len(skipped) > 0:
        for file in skipped:
            print '%s: unable to chown %s:%s, skipped' % (file, user, group)


def write_configuration(fileobject, sections):
    from ConfigParser import RawConfigParser
    config = RawConfigParser()

    for section in sections:
        config.add_section(section)
        for item in sections[section].items():
            config.set(section, *item)

    config.write(fileobject)


class SiteSubFolder(object):
    name = ''
    def __init__(self, base):
        self._prefix = os.path.join(
            os.path.abspath(base), self.name)

    @property
    def prefix(self):
        return self._prefix

    def populate(self):
        pass

    def create(self):
        try:
            os.makedirs(self.prefix)
        except OSError:
            pass


class Conf(SiteSubFolder):
    name = 'conf'
    def populate(self):
        pass


class Files(SiteSubFolder):
    name = 'files'
    def populate(self):
        make_directory_tree(self.prefix, ['uploads', 'downloads'])
        chown_folder_and_files(self.prefix, 'nobody', 'nobody')


class Static(SiteSubFolder):
    name = 'static'
    def populate(self):
        src_dir = os.path.join(_PACKAGE_PREFIX, 'data', 'static')
        copy_dir_contents(src_dir, self.prefix)


class Logs(SiteSubFolder):
    name = 'logs'
    def populate(self):
        make_directory_tree(self.prefix, [])
        chown_folder_and_files(self.prefix, 'nobody', 'nobody')


class Templates(SiteSubFolder):
    name = 'templates'
    def populate(self):
        src_dir = os.path.join(_PACKAGE_PREFIX, 'data', 'templates')
        copy_dir_contents(src_dir, self.prefix)


class Database(SiteSubFolder):
    name = 'db'
    def populate(self):
        src_dir = os.path.join(_PACKAGE_PREFIX, 'data', 'db')

        for db in ['wmt', 'users', 'submission']:
            create_empty_database(
                os.path.join(self.prefix, db + '.db'),
                schema=os.path.join(src_dir, db + '.sql'),
                clobber=True)
        chown_folder_and_files(self.prefix, 'nobody', 'nobody')

        download_and_unpack_from_github('csdms/component_metadata',
                                        os.path.join(self.prefix, 'components'))


class Bin(SiteSubFolder):
    name = 'bin'
    def populate(self):
        import shutil
        shutil.copyfile(
            os.path.join(_PACKAGE_PREFIX, 'scripts', 'wmt_wsgi_main.py'),
            os.path.join(self.prefix, 'start_wmt.wsgi'))


class Site(object):
    def __init__(self, prefix, options={}):
        self._prefix = os.path.abspath(prefix)
        self._dirs = {
            'templates': Templates(self.prefix),
            'conf': Conf(self.prefix),
            'db': Database(self.prefix),
            'bin': Bin(self.prefix),
            'files': Files(self.prefix),
            'static': Static(self.prefix),
            'logs': Logs(self.prefix),
        }
        self._options = options

    @property
    def prefix(self):
        return self._prefix

    @property
    def dir(self):
        return self._dirs

    def create(self):
        for dir in self._dirs.values():
            dir.create()
            dir.populate()

        self.to_conf_file(os.path.join(self.dir['conf'].prefix, 'wmt.ini'))

    def to_conf_file(self, filename):
        sections = OrderedDict([
            ('paths', OrderedDict([
                #('prefix', self.prefix),
                ('templates', self.dir['templates'].name),
                ('files', self.dir['files'].name),
                ('uploads', os.path.join(self.dir['files'].name, 'uploads')),
                ('downloads', os.path.join(self.dir['files'].name, 'downloads')),
                ('static', self.dir['static'].name),
                ('logs', self.dir['logs'].name),
                ('database', os.path.join('%(db)s', 'wmt.db')),
                ('user_db', os.path.join('%(db)s', 'users.db')),
                ('submission_db', os.path.join('%(db)s', 'submission.db')),
                ('db', self.dir['db'].name), ])
            ),
            ('url', OrderedDict([
                ('scheme', self._options.pop('url_scheme')),
                ('netloc', self._options.pop('url_netloc')),
                ('path', self._options.pop('url_path')), ]),
            ),
            ('passlib', OrderedDict([
                ('schemes', 'sha512_crypt, sha256_crypt'),
                ('sha256_crypt__default_rounds', '100000'),
                ('sha512_crypt__default_rounds', '100000'), ])
            ),
        ])
        sections['user'] = OrderedDict(self._options)

        with open(filename, 'w') as conf_file:
            write_configuration(conf_file, sections)
