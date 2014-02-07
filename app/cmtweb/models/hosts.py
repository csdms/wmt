import web
from ..config import (db, site)


def new_host(host, username, password, command):
    from sqlite3 import IntegrityError

    hashed_password = site['pw'].encrypt(password)
    db.insert('hosts', username=username, password=hashed_password,
              command=command, host=host)


def get_hosts():
    return db.select('hosts', order='host DESC')


def get_host(id):
    try:
        return db.select('hosts', where='id=$id', vars=locals())[0]
    except IndexError:
        return None


#def create_account(username, password):
#    db.insert('users', username=username, password=password)


#def get_user_by_name(username):
#    return web.listget(
#        db.select('users', vars=dict(username=username),
#                  where='username=$username', 0, {}))


#def get_user_by_id(id):
#    return web.listget(
#        db.select('users', vars=dict(id=id), where='id=$id', 0, {}))


def is_correct_password(username, password):
    user = get_user_by_name(username)
    return user.get('password', False) == password


def update_host(id, **kwds):
    db.update('hosts', vars=dict(id=id), where='id=$id', **kwds)


