import web
import urlparse

from .config import site

parts = urlparse.urlsplit('https://csdms.colorado.edu/wmt-server')
globals = {
    'SCHEME': parts.scheme,
    'HOST': parts.netloc,
    'PREFIX': parts.path,
    'BASE_URL': urlparse.urlunsplit(parts),
}

render = web.template.render(site['templates'], base='base', globals=globals)
