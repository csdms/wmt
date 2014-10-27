from standard_names import StandardName

from ..config import names_db as db


def get_names():
    return db.select('names', order='id DESC')


def get_name(id):
    try:
        name = db.select('names', where='id=$id', vars=locals())[0]
    except IndexError:
        raise
        #raise BadIdError(id)

    return name


def to_resource(name):
    sn = StandardName(name['name'])
    return {
        'id': name.id,
        'href': '/api/names/%d' % name.id,
        'name': name.name,
        'object': sn.object,
        'quantity': sn.quantity,
        'operators': sn.operators,
    }


def from_resource(resource):
    return {
        'id': resource['id'],
        'name': resource['name'],
    }
