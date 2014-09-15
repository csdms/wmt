import os
import web
import json
from collections import namedtuple

from ..paths import DATABASE_DIR


Entry = namedtuple('Entry', ['name', 'content', 'id'])


class Database(object):
    def __init__(self, db='.'):
        self._db = os.path.abspath(db)
        self._id = self._init_count()

    @property
    def count_file(self):
        return os.path.join(self._db, '.count')

    @property
    def id(self):
        return str(self._id)

    def _increment_count(self):
        self._id += 1
        with open(self.count_file, 'w') as file:
            file.write(self.id)

    def _init_count(self):
        if not os.path.isfile(self.count_file):
            with open(self.count_file, 'w') as file:
                file.write('0')

        with open(self.count_file, 'r') as file:
            count = int(file.read())

        return count

    def _all_ids(self):
        ids = []
        for entry in os.listdir(self._db):
            try:
                ids.append(int(entry))
            except ValueError:
                pass
        return ids

    def insert(self, **kwds):
        self._increment_count()
        with open(os.path.join(self._db, self.id), 'w') as file:
            kwds['id'] = self.id
            file.write(json.dumps(Entry(**kwds)))

    def delete(self, id):
        os.remove(os.path.join(self._db, str(id)))

    def select(self, id=None):
        if id is None:
            ids = self._all_ids()
            entries = []
            for id in ids:
                try:
                    entry = self.select(str(id))
                except ValueError:
                    pass
                else:
                    entries.append(entry)
            return entries
        else:
            with open(os.path.join(self._db, str(id)), 'r') as file:
                try:
                    project = json.loads(file.read())
                except ValueError:
                    raise
                else:
                    return Entry(*project)

    def update(self, id, **kwds):
        with open(os.path.join(self._db, self.id), 'w') as file:
            kwds['id'] = str(id)
            file.write(json.dumps(Entry(**kwds)))


db = Database(db=DATABASE_DIR)


def new_project(name, text):
    db.insert(name=name, content=text)


def del_project(id):
    db.delete(id)


def update_project(id, name, text):
    db.update(id, name=name, content=text)


def get_projects():
    return db.select()


def get_project(id):
    try:
        return db.select(id)
    except IndexError:
        return None
