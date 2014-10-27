import os
import web

from ..config import tag_db as db
from ..session import get_username


class Error(Exception):
    pass


class BadIdError(Error):
    def __init__(self, id):
        self._id = id

    def __str__(self):
        return str(self._id)


class AuthorizationError(Error):
    def __init__(self, id):
        self._id = id

    def __str__(self):
        return str(self._id)


class TagExistsError(Error):
    def __init__(self, tag, owner):
        self._tag = tag
        self._owner = owner

    def __str__(self):
        return '%s:%s' % (self._owner, self._tag)


class TagNameError(Error):
    def __init__(self, tag):
        self._tag = tag

    def __str__(self):
        return '%s' % self._tag


def to_resource(tag):
    return {
        '_type': 'tag',
        'id': tag.id,
        'href': '/api/tags/%d' % tag.id,
        'tag': tag.tag,
        'owner': tag.owner,
    }


def from_resource(resource):
    return {
        'id': resource['id'],
        'tag': resource['tag'],
        'owner': resource['owner'],
    }


def new_tag(tag, owner=''):
    try:
        return get_tag_by_name(tag)
    except TagNameError:
        return db.insert('tags', tag=tag, owner=owner)


def del_tag(id):
    db.delete('tags', where='id=$id', vars=locals())
    db.delete('model_tags', where='tag_id=$id', vars=locals())


def del_tag_name(tag):
    try:
        db.select('tags', where='owner=$owner AND tag=$tag', vars=locals())[0]
    except IndexError:
        pass
    else:
        db.delete('tags', where='tag=$tag AND owner=$owner',
                  vars=dict(owner=get_username(), tag=tag))


def get_public_tags():
    return db.select('tags', order='id DESC', where='owner=$owner',
                     vars=dict(owner=''))


def get_private_tags():
    return db.select('tags', order='id DESC', where='owner=$owner',
                     vars=dict(owner=get_username()))


def get_tags():
    where = ' OR '.join(['owner=$owner', 'owner=\'\''])
    return db.select('tags', order='id DESC', where=where,
                     vars=dict(owner=get_username()))


def get_tag_names():
    tags = get_tags()
    return [tag.tag for tag in tags]


def get_tag(id):
    try:
        tag = db.select('tags', where='id=$id', vars=locals())[0]
    except IndexError:
        raise BadIdError(id)

    if tag['owner'] in ['', get_username()]:
        return tag
    else:
        raise AuthorizationError(id)


def get_tag_by_name(name):
    try:
        tag = db.select('tags', where="tag=$tag AND (owner=$owner OR owner='')",
                       vars=dict(owner=get_username(), tag=name))[0]
    except IndexError:
        raise TagNameError(name)

    return tag.id


def tag_model(model, tag):
    try:
        get_tag(tag)
    except (BadIdError, AuthorizationError):
        raise BadIdError(tag)
    else:
        if tag not in get_model_tags(model):
            db.insert('model_tags', model_id=model, tag_id=tag)


def untag_model(model, tag):
    db.delete('model_tags', where='model_id=$model AND tag_id=$tag',
              vars=locals())


def select_models_tagged_with(tag):
    return [entry.model_id for entry in db.select('model_tags', where='tag_id=$tag', vars=dict(tag=tag))]


def select_public_models():
    public_id = get_tag_by_name('public')
    return select_models_tagged_with(public_id)


def select_model(tags, sortby=None):
    from . import models


    selected = set([entry.model_id for entry in db.select('model_tags')])
    for tag in tags:
        selected &= set(select_models_tagged_with(tag))

    bad_ids = []
    for id in selected:
        try:
            models.get_model(id)
        except models.BadIdError:
            bad_ids.append(id)
    for id in bad_ids:
        selected.remove(id)

    selected = list(selected)
    if sortby:
        selected.sort(key=lambda id: models.get_model(id)[sortby])

    return selected


def get_model_tags(model):
    entries = db.select('model_tags', where='model_id=$model', vars=locals())
    return [entry.tag_id for entry in entries]
