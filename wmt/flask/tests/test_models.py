import json

from wmt.flask import create_app
from nose.tools import (assert_equal, assert_is_instance, assert_dict_equal,
                        assert_list_equal)

from .tools import assert_404_not_found, assert_200_success, login_or_fail
from . import app, FAKE_BLUEPRINT, FAKE_MODEL, FAKE_USER


def assert_is_model_resource(model, **kwds):
    assert_is_instance(model, dict)
    assert_equal(set(model.keys()),
                 set(['@type', 'href', 'id', 'name', 'user', 'date', 'links']))
    assert_equal(model['@type'], 'model')
    for k, v in kwds.items():
        assert_equal(model[k], v)


def test_show():
    with app.test_client() as c:
        models = json.loads(c.get('/models/').data)
    assert_is_instance(models, list)
    for model in models:
        assert_is_model_resource(model)


def test_model():
    with app.test_client() as c:
        assert_404_not_found(c.get('/models/0'))
        model = json.loads(c.get('/models/1').data)
    assert_is_model_resource(model, id=1)


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
