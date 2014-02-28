import web
import datetime
import uuid

from ..config import submission_db as db


def new(name, model_id):
    now = web.net.httpdate(datetime.datetime.now())
    data = {
        'name': name,
        'model_id': model_id,
        'uuid': str(uuid.uuid4()),
        'status': 'submitted',
        'message': 'Run has been submitted',
        'created': now,
        'updated': now,
        'owner': 'anonymous',
    }
    db.insert('submission', **data)
    return data['uuid']


def update(uuid, **kwds):
    kwds['updated'] = web.net.httpdate(datetime.datetime.now())
    db.update('submission', vars=dict(uuid=uuid), where='uuid=$uuid', **kwds)


def get_submissions():
    return db.select('submission', order='id DESC')


def get_submission(uuid):
    try:
        return db.select('submission', where='uuid=$uuid', vars=locals())[0]
    except IndexError:
        raise BadIdError(id)


def get_status(uuid):
    return db.select('submission', what='status', where='uuid=$uuid', vars=locals())
