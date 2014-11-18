import json
from uuid import uuid4

from wmt.flask import create_app
from nose.tools import (assert_equal, assert_is_instance, assert_dict_equal,
                        assert_list_equal)

from .tools import (assert_401_unauthorized, assert_404_not_found,
                    assert_403_forbidden, assert_200_success,
                    assert_422_unprocessable_entity, loads_if_assert_200,
                    json_post, json_delete, login_or_fail)
from . import (app, FAKE_TAG, FAKE_USER, FAKE_USER_NAME, FAKE_USER1_NAME,
               FAKE_USER1_PASS)



def test_show():
    with app.test_client() as c:
        tags = json.loads(c.get('/tags/').data)
    assert_is_instance(tags, list)
    for tag in tags:
        assert_equal(tag['_type'], 'tag')


def test_get_existing():
    expected = {
        u'_type': u'tag',
        u'href': u'/tags/1',
        u'tag': u'foobar',
        u'id': 1,
        u'user': {u'href': u'/users/1'}
    }
    with app.test_client() as c:
        tag = loads_if_assert_200(c.get('/tags/1'))

    assert_dict_equal(tag, expected)


def test_get_non_existing():
    with app.test_client() as c:
        assert_404_not_found(c.get('/tags/0'))


def test_new_and_delete():
    tag_name = str(uuid4())

    with app.test_client() as c:
        login_or_fail(c, **FAKE_USER)
        added = loads_if_assert_200(
            json_post(c, '/tags/', data=dict(tag=tag_name)))
    assert_equal(added['tag'], tag_name)

    with app.test_client() as c:
        login_or_fail(c, **FAKE_USER)
        deleted = loads_if_assert_200(json_delete(c, added['href']))
    assert_equal(deleted, added)


def test_new_not_logged_in():
    with app.test_client() as c:
        assert_401_unauthorized(
            json_post(c, '/tags/', data=dict(tag='a-new-tag')))


def test_new_existing():
    with app.test_client() as c:
        login_or_fail(c, **FAKE_USER)
        assert_422_unprocessable_entity(
            json_post(c, '/tags/', data=dict(tag=FAKE_TAG['tag'])))


def test_delete_non_existing():
    with app.test_client() as c:
        login_or_fail(c, **FAKE_USER)
        assert_404_not_found(json_delete(c, '/tags/999999'))


def test_delete_not_logged_in():
    with app.test_client() as c:
        assert_401_unauthorized(json_delete(c, '/tags/1'))


def test_delete_wrong_user():
    with app.test_client() as c:
        login_or_fail(c, username=FAKE_USER1_NAME, password=FAKE_USER1_PASS)
        assert_403_forbidden(json_delete(c, '/tags/1'))
