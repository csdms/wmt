import web
import urlparse

from .config import site

#parts = urlparse.urlsplit('https://csdms.colorado.edu/wmt-server')
parts = (site['scheme'], site['netloc'], site['path'], '', '')

globals = {
    'SCHEME': parts[0],
    'HOST': parts[1],
    'PREFIX': '/' + parts[2],
    'BASE_URL': urlparse.urlunsplit(parts),
}

render = web.template.render(site['templates'], base='base', globals=globals)
