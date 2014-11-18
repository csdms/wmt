import pkgutil
import importlib

from flask import Blueprint


def register_blueprints(app, package_name, package_path):
    rv = []

    #for _, name, _ in pkgutil.iter_modules(package_path):
    for name in ['users', 'names', 'tags', 'sims', 'components', 'models']:
        m = importlib.import_module('%s.api.%s' % (package_name, name))
        for item in dir(m):
            item = getattr(m, item)
            if isinstance(item, Blueprint):
                #print 'registering blueprint for %s' % item.name
                app.register_blueprint(item, url_prefix='/' + item.name)
            rv.append(item)

    return rv
