import web
from .config import site


def add_sessions_to_app(app):
    import os

    db = web.database(dbn='sqlite', db=os.path.join(site['db'], 'session.db'))
    store = web.session.DBStore(db, 'session')
    session = web.session.Session(app, store, initializer={
        'loggedin': False,
        'username': ''})

    def session_hook():
        web.ctx.session = session

    app.add_processor(web.loadhook(session_hook))


def get_session():
    return web.ctx.session


def get_username():
    return get_session().username


def login(username):
    session = get_session()
    session.loggedin = True
    session.username = username


def logout():
    session = get_session()
    session.loggedin = False
    session.username = ''
