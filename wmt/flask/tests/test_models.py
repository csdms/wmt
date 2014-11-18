import json

from wmt.flask import create_app
from nose.tools import (assert_equal, assert_is_instance, assert_dict_equal,
                        assert_list_equal)

from .tools import assert_404_not_found, assert_200_success, login_or_fail
from . import app, FAKE_BLUEPRINT, FAKE_MODEL, FAKE_USER


def test_show():
    with app.test_client() as c:
        models = json.loads(c.get('/models/').data)
    assert_is_instance(models, list)


def test_model():
    with app.test_client() as c:
        assert_404_not_found(c.get('/models/0'))
        model = json.loads(c.get('/models/1').data)

    assert_equal(model['_type'], 'model')
    assert_equal(model['href'], '/api/models/1')
    assert_equal(model['id'], 1)
    assert_equal(model['owner'], FAKE_USER['username'])


def test_new():
    data = json.dumps(dict(name='baz', json=json.dumps(FAKE_BLUEPRINT)))

    with app.test_client() as c:
        login_or_fail(c, **FAKE_USER)
        resp = c.post('/models/', data=data,
                      headers={'Content-type': 'application/json'})
        assert_200_success(resp)

    model = json.loads(resp.data)
    assert_equal(model['name'], 'baz')

    with app.test_client() as c:
        resp = c.get('/models/%d' % model['id'])
    assert_dict_equal(json.loads(resp.data), model)


def test_add_tag():
    with app.test_client() as c:
        login_or_fail(c, **FAKE_USER)
        data = json.dumps(dict(id=1))
        resp = c.post('/models/1/tags', data=data,
                      headers={'Content-type': 'application/json'})
