import web
import datetime
import uuid

from ..config import submission_db as db


def new(name):
    now = web.net.httpdate(datetime.datetime.now())
    data = {
        'name': name,
        'uuid': str(uuid.uuid4()),
        'status': 'submitted',
        'message': 'Run has been submitted',
        'created': now,
        'updated': now,
        'owner': 'anonymous',
    }
    return db.insert('submission', **data)


def update(id, **kwds):
    kwds['updated'] = web.net.httpdate(datetime.datetime.now())
    db.update('submission', vars=dict(id=id), where='id=$id', **kwds)


def get_submissions():
    return db.select('submission', order='id DESC')


def get_submission(id):
    try:
        return db.select('submission', where='id=$id', vars=locals())[0]
    except IndexError:
        raise BadIdError(id)


def get_status(id):
    return db.select('submission', what='status', where='id=$id', vars=locals())
