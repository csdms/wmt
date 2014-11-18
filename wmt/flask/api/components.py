from flask import Blueprint
from flask import g, request, abort

from ..utils import as_resource, as_collection
from ..services import components

components_page = Blueprint('components', __name__)


@components_page.route('/', methods=['GET', 'OPTIONS'])
def show():
    return as_collection(components.get_names(sort=True))


@components_page.route('/<name>', methods=['GET', 'OPTIONS'])
def component(name):
    key = request.args.get('key', None)

    comp = components.get_or_404(name)

    if key is not None:
        return as_resource(comp.get(key, None) or abort(400))
    else:
        resp = {}
        for key in ['doi', 'author', 'url', 'summary', 'version', 'id', ]:
            resp[key] = comp[key]
        return as_resource(resp)


@components_page.route('/<name>/params', methods=['GET', 'OPTIONS'])
def component_params(name):
    comp = components.get_or_404(name)

    return as_collection(comp['parameters'])


@components_page.route('/<name>/params/<param>', methods=['GET', 'OPTIONS'])
def component_param(name, param):
    comp = comp_db.get_or_404(name)

    for p in comp['parameters']:
        if p['key'] == param:
            return as_resource(p)

    abort(404)


@components_page.route('/<name>/files', methods=['GET', 'OPTIONS'])
def component_files(name):
    return as_collection(components.input(name) or abort(404))


@components_page.route('/<name>/files/<file>', methods=['GET', 'OPTIONS'])
def component_file(name, file):
    files = components.input(name) or abort(404)

    return as_resource(files.get(file, None) or abort(404))


@components_page.route('/<name>/inputs', methods=['GET', 'OPTIONS'])
def component_inputs(name):
    comp = components.get_or_404(name)
    return as_resource(comp.get('uses', []))


@components_page.route('/<name>/outputs', methods=['GET', 'OPTIONS'])
def component_outputs(name):
    comp = components.get_or_404(name)
    return as_resource(comp.get('provides', []))
