import json
from nose.tools import (assert_equal, assert_true, assert_is_instance,
                        assert_less_equal)
from . import FAKE_USER_NAME, FAKE_USER_PASS


class ResourceValidator(object):
    __meta_fields__ = set()
    __type__ = None
    __fields__ = set()

    def __init__(self, type=None, fields=None):
        self._fields = set(fields or self.__fields__)
        self._type = type or self.__type__

    def __call__(self, resource, **kwds):
        assert_is_instance(resource, dict)

        actual = set(resource.keys())
        expected = set(self._fields) | set(self.__meta_fields__)

        assert_equal(expected, actual)
        assert_less_equal(set(kwds.keys()), expected)

        for k, v in kwds.items():
            assert_equal(resource[k], v)


class CollectionValidator(object):
    __validator__ = ResourceValidator()

    def __init__(self, validator=None):
        self._validator = validator or self.__validator__

    def __call__(self, collection, **kwds):
        assert_is_instance(collection, list)
        if self.__validator__:
            for item in collection:
                self.__validator__(item, **kwds)


class AssertIsResourceResponse(ResourceValidator):
    __meta_fields__ = ['@type']

    def __call__(self, resp, **kwds):
        if hasattr(resp, 'data'):
            try:
                resource = json.loads(resp.data)
            except ValueError:
                raise AssertionError('not a JSON object')
        else:
            resource = resp
        super(AssertIsResourceResponse, self).__call__(resource, **kwds)


class AssertIsCollectionResponse(CollectionValidator):
    def __call__(self, resp, **kwds):
        if hasattr(resp, 'data'):
            try:
                collection = json.loads(resp.data)
            except ValueError:
                raise AssertionError('not a JSON object')
        super(AssertIsCollectionResponse, self).__call__(collection, **kwds)


assert_is_resource = AssertIsResourceResponse()
assert_is_collection = AssertIsCollectionResponse()


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


def assert_success(resp):
    assert_equal((resp.status_code - 200) // 100, 0)


def assert_204_empty(resp):
    assert_equal(resp.status_code, 204)
    assert_equal(resp.data, "")


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
