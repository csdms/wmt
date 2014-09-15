import json


def check_json_is_valid(text):
    try:
        model = json.loads(text)
    except ValueError:
        return False

    try:
        return model.has_key('model')
    except AttributeError:
        return False


print check_json_is_valid(open('test.txt', 'r').read())
