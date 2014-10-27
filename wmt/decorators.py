import web
import json
from functools import wraps


def as_json(func):
    @wraps(func)
    def _wrapper(*args, **kwds):
        web.header('Content-Type', 'application/json; charset=utf-8')
        ans = func(*args, **kwds)
        if ans is None:
            raise web.nocontent()
        return json.dumps(ans, sort_keys=True, indent=4,
                          separators=(',', ': '))
    return _wrapper
