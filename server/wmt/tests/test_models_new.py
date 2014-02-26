import requests

from nose.tools import *


url_base = 'https://csdms.colorado.edu/wmt/'


def test_new_model():
    data = dict(name='Test', json='{"model": 0}')
    resp = requests.post(url_base + 'models/new', data=data)

    assert_equal(resp.status_code, 200)
    assert_is_instance(int(resp.text), int)

def test_new_model_bad_json():
    data = dict(name='Test', json='0')
    resp = requests.post(url_base + 'models/new', data=data)

    assert_equal(resp.status_code, 200)
    assert_is_instance(int(resp.text), int)
