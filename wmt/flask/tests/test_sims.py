import json
from uuid import uuid4

from wmt.flask import create_app
from nose.tools import (assert_equal, assert_is_instance, assert_dict_equal,
                        assert_list_equal, assert_less_equal)

from .tools import (assert_401_unauthorized, assert_404_not_found,
                    assert_403_forbidden, assert_200_success,
                    assert_204_empty, loads_if_assert_200,
                    assert_422_unprocessable_entity,
                    json_post, json_delete, login_or_fail,
                    AssertIsResourceResponse, AssertIsCollectionResponse)
from . import (app, FAKE_SIM, FAKE_SIM_NAME, FAKE_SIM_MODEL, FAKE_USER,
               FAKE_USER_NAME, FAKE_USER1_NAME, FAKE_USER1_PASS)


class AssertIsSimResource(AssertIsResourceResponse):
    __type__ = 'sim'
    __fields__ = set(['href', 'id', 'name', 'owner', 'status', 'message',
                      'user', 'model'])
                      


class AssertIsSimCollection(AssertIsCollectionResponse):
    __validator__ = AssertIsSimResource()


assert_is_sim_resource = AssertIsSimResource()
assert_is_sim_collection = AssertIsSimCollection()


def test_show():
    with app.test_client() as c:
        resp = c.get('/sims/')
        assert_200_success(resp)
        assert_is_sim_collection(resp)


def test_get_existing():
    with app.test_client() as c:
        resp = c.get('/sims/1')
        assert_200_success(resp)
        assert_is_sim_resource(resp, name='foobar')


def test_get_non_existing():
    with app.test_client() as c:
        assert_404_not_found(c.get('/sims/0'))


def test_new_and_delete():
    sim_name = str(uuid4())

    with app.test_client() as c:
        login_or_fail(c, **FAKE_USER)
        resp = json_post(c, '/sims/', data=dict(name=sim_name, model=1))
        assert_200_success(resp)
        assert_is_sim_resource(resp, name=sim_name)
        new_sim = json.loads(resp.data)

    with app.test_client() as c:
        login_or_fail(c, **FAKE_USER)
        assert_204_empty(json_delete(c, new_sim['href']))


def test_new_not_logged_in():
    with app.test_client() as c:
        assert_401_unauthorized(
            json_post(c, '/sims/', data=dict(name='a-new-sim')))


def test_new_existing():
    with app.test_client() as c:
        login_or_fail(c, **FAKE_USER)
        assert_200_success(
            json_post(c, '/sims/', data=dict(name=FAKE_SIM_NAME,
                                             model=FAKE_SIM_MODEL)))


def test_delete_non_existing():
    with app.test_client() as c:
        login_or_fail(c, **FAKE_USER)
        assert_404_not_found(json_delete(c, '/sims/999999'))


def test_delete_not_logged_in():
    with app.test_client() as c:
        assert_401_unauthorized(json_delete(c, '/sims/1'))


def test_delete_wrong_user():
    with app.test_client() as c:
        login_or_fail(c, username=FAKE_USER1_NAME, password=FAKE_USER1_PASS)
        assert_403_forbidden(json_delete(c, '/sims/1'))
