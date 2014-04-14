import web
import urlparse

from .config import site
from .session import get_session


parts = (site['scheme'], site['netloc'], site['path'], '', '')

globals = {
    'SCHEME': parts[0],
    'HOST': parts[1],
    'PREFIX': '/' + parts[2],
    'BASE_URL': urlparse.urlunsplit(parts),
    'CONTEXT': get_session(),
}

render = web.template.render(site['templates'], base='base', globals=globals)
