import web

from .config import site


render = web.template.render(site['templates'], base='base')
