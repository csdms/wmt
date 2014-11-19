import json

from nose.tools import (assert_equal, assert_is_instance, assert_dict_equal,
                        assert_list_equal)
from .tools import (assert_404_not_found, assert_401_unauthorized,
                    assert_403_forbidden, login_or_fail, assert_200_success,
                    assert_422_unprocessable_entity)
from . import app, FAKE_USER


def new_user(name, password):
    data = json.dumps({'username': name, 'password': password})

    with app.test_client() as c:
        return c.post('/users/', data=data,
                      headers={'Content-type': 'application/json'})


def new_user_or_fail(name, password):
    resp = new_user(name, password)
    assert_200_success(resp)
    user = json.loads(resp.data)
    return user['href']


def test_whoami():
    with app.test_client() as c:
        assert_equal(json.loads(c.get('/users/whoami').data), "guest")
        login_or_fail(c, **FAKE_USER)
        assert_equal(json.loads(c.get('/users/whoami').data),
                     FAKE_USER['username'])


def test_login():
    username, password = FAKE_USER['username'], FAKE_USER['password']
    url = '/users/login?username=%s&password=%s' % (username, password)

    with app.test_client() as c:
        assert_equal(json.loads(c.get(url).data), username) 
        assert_equal(json.loads(c.get('/users/whoami').data), username)
        assert_equal(json.loads(c.get('/users/logout').data), "guest")
        assert_equal(json.loads(c.get('/users/whoami').data), "guest")


def test_login_bad_username():
    username, password = 'wrong-user', 'wrong-passwprd'
    url = '/users/login?username=%s&password=%s' % (username, password)

    with app.test_client() as c:
        assert_401_unauthorized(c.get(url))


def test_login_bad_password():
    username, password = FAKE_USER['username'], 'wrong-password'
    url = '/users/login?username=%s&password=%s' % (username, password)

    with app.test_client() as c:
        assert_401_unauthorized(c.get(url))


def test_logout():
    with app.test_client() as c:
        login_or_fail(c, **FAKE_USER)
        assert_equal(json.loads(c.get('/users/whoami').data),
                     FAKE_USER['username'])

        for _ in xrange(10):
            c.get('/users/logout')
            assert_equal(json.loads(c.get('/users/whoami').data), "guest")


def test_show():
    with app.test_client() as c:
        users = json.loads(c.get('/users/').data)
        assert_is_instance(users, list)
        for user in users:
            assert_equal(user['_type'], 'user')


def test_user():
    expected = {u'_type': u'user',
                u'href': u'/users/1',
                u'id': 1,
                u'links': [
                    {u"rel": u"resource/tags", u"href": u"/tags/1"},
                    {u"rel": u"resource/models", u"href": u"/models/1"},
                    {u"rel": u"resource/models", u"href": u"/models/2"}
                ],
                u'username': FAKE_USER['username'],
               }
    with app.test_client() as c:
        user = json.loads(c.get('/users/1').data)
        for k in ['_type', 'href', 'username']:
            assert_equal(user[k], expected[k])


def test_user_that_does_not_exist():
    with app.test_client() as c:
        assert_404_not_found(c.get('/users/0'))
        assert_404_not_found(c.get('/users/99999999'))


def test_new():
    data = json.dumps({'username': 'foobar@baz.com', 'password': 'foobar'})
    with app.test_client() as c:
        resp = c.post('/users/', data=data,
                      headers={'Content-type': 'application/json'})
        assert_200_success(resp)

        user = json.loads(resp.data)

        assert_equal(user['username'], 'foobar@baz.com')

        assert_dict_equal(json.loads(c.get(user['href']).data), user)

        c.delete(user['href'], data=json.dumps({'password': 'foobar'}),
                 headers={'Content-type': 'application/json'})
        assert_404_not_found(c.get(user['href']))


def test_new_existing():
    name, password = FAKE_USER['username'], 'a-different-password'

    with app.test_client() as c:
        existing = json.loads(c.get('/users/search?username=%s' % name).data)

    assert_equal(len(existing), 1)
    assert_422_unprocessable_entity(new_user(name, password))


def test_delete():
    href = new_user_or_fail('foobar@delete.baz.com', 'foobar')
    with app.test_client() as c:
        c.delete(href, data=json.dumps({'password': 'foobar'}))
        assert_404_not_found(c.get(href))


def test_delete_bad_user():
    with app.test_client() as c:
        assert_404_not_found(c.delete('/users/999999999'))


def test_delete_bad_password():
    href = new_user_or_fail('foobar@delete.baz.com', 'foobar')
    with app.test_client() as c:
        resp = c.delete(href, data=json.dumps({'password': 'wrong-password'}))
    assert_401_unauthorized(resp)


def test_search_for_existing():
    name = FAKE_USER['username']
    with app.test_client() as c:
        users = json.loads(c.get('/users/search?username=%s' % name).data)
    assert_equal(len(users), 1)
    assert_equal(users[0]['username'], name)


def test_search_for_non_existing():
    name = 'not-a-name@example.com'
    with app.test_client() as c:
        users = json.loads(c.get('/users/search?username=%s' % name).data)
    assert_list_equal(users, [])


from threading import Thread


def _add_user(app, name):
    data = json.dumps({'username': name, 'password': 'foobar'})
    with app.test_client() as c:
        resp = c.post('/users/', data=data,
                      headers={'Content-type': 'application/json'})

        while resp.status_code == 503:
            resp = c.post('/users/', data=data,
                          headers={'Content-type': 'application/json'})


def test_asynchronous_new():
    names = ['foo@bar.baz%d' % id for id in xrange(100)]

    for name in names:
        thr = Thread(target=_add_user, args=[app, name])
        thr.start()

    for name in names:
        with app.test_client() as c:
            users = json.loads(c.get('/users/search?username=%s' % name).data)
            assert_equal(len(users), 1)
            c.delete(users[0]['href'], data=json.dumps({'password': 'foobar'}),
                     headers={'Content-type': 'application/json'})
            assert_404_not_found(c.get(users[0]['href']))
