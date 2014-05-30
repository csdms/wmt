import web

from ..config import user_db as db
from ..config import site


def new_user(username, password):
    from sqlite3 import IntegrityError

    hashed_password = site['pw'].encrypt(password)
    try:
        db.insert('users', username=username.lower(), password=hashed_password)
    except IntegrityError:
        pass


def get_users():
    return db.select('users', order='username DESC')


def get_user(username):
    _username = username.lower()
    try:
        return db.select('users', where='username=$_username', vars=locals())[0]
    except IndexError:
        return None


def create_account(username, password):
    db.insert('users', username=username, password=password)


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


def update(id, **kwds):
    db.update('users', vars=dict(id=id), where='id=$id', **kwds)


def change_password(id, password):
    hashed_password = site['pw'].encrypt(password)
    db.update('users', vars=dict(id=id), where='id=$id', password=hashed_password)
