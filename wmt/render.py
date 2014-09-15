import web
import urlparse

from .config import site
from .session import get_session


parts = (site['scheme'], site['netloc'], site['path'], '', '')
pickup_parts = (site['pickup_scheme'], site['pickup_netloc'],
                site['pickup_path'], '', '')

globals = {
    'SCHEME': parts[0],
    'HOST': parts[1],
    'PREFIX': '/' + parts[2],
    'BASE_URL': urlparse.urlunsplit(parts),
    'CONTEXT': get_session(),
    'PICKUP_SCHEME': pickup_parts[0],
    'PICKUP_HOST': pickup_parts[1],
    'PICKUP_PREFIX': '/' + pickup_parts[2],
}

render = web.template.render(site['templates'], base='base', globals=globals)
