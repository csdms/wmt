import json
from nose.tools import assert_equal
from . import FAKE_USER_NAME, FAKE_USER_PASS


def assert_401_unauthorized(resp):
    assert_equal(resp.status_code, 401)


def assert_403_forbidden(resp):
    assert_equal(resp.status_code, 403)


def assert_404_not_found(resp):
    assert_equal(resp.status_code, 404)


def assert_422_unprocessable_entity(resp):
    assert_equal(resp.status_code, 422)


def assert_200_success(resp):
    assert_equal(resp.status_code, 200)


def loads_if_assert_200(resp):
    assert_200_success(resp)
    return json.loads(resp.data)


def prepare_kwds(**kwds):
    if 'data' in kwds:
        kwds['data'] = json.dumps(kwds['data'])
    if 'headers' in kwds:
        kwds['headers'].setdefault('Content-type', 'application/json')
    else:
        kwds['headers'] = {'Content-type': 'application/json'}
    return kwds


def json_post(c, url, **kwds):
    return c.post(url, **prepare_kwds(**kwds))


def json_get(c, url, **kwds):
    return c.get(url, **prepare_kwds(**kwds))


def json_delete(c, url, **kwds):
    return c.delete(url, **prepare_kwds(**kwds))


def login_or_fail(c, username=FAKE_USER_NAME, password=FAKE_USER_PASS):
    resp = c.get('/users/login?username=%s&password=%s' % (username, password))
    assert_200_success(resp)
    return resp


def logout():
    app.get('/users/logout')
