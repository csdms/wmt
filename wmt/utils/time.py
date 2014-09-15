import web
from datetime import datetime


def current_time_as_string(format='iso'):
    assert(format in ['iso', 'web'])

    if format == 'iso':
        return datetime.now().isoformat(' ')
    else:
        web.net.httpdate(datetime.now())
