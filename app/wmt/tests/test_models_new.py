from paste.fixture import TestApp
from nose.tools import *
from wmt.scripts.wmt_wsgi_main import application


class TestWmt():
    def test_new(self):
        test_app = TestApp(application)

        resp = test_app.post('models/new/', name='test',
                             json='{"model": 0}')

        assert_equal(resp, 9)
