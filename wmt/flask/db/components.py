import os

from flask import current_app

from ...utils.db import load_palette


#DATABASE_DIR = '/data/web/htdocs/wmt/api/v1/db'
PALETTE = load_palette(os.path.join(current_app.config['DATABASE_DIR'],
                                    'components'))


def get_components():
    return PALETTE.values()


def get_component(name):
    return PALETTE.get(name, None)


def get_component_names(sort=False):
    names = list(PALETTE.keys())
    if sort:
        names.sort()
    return names


def _read_input_file(name, filename):
    input_file_dir = os.path.join(current_app.config['DATABASE_DIR'],
                                  'components', name, 'files')
    path_to_file = os.path.join(input_file_dir, filename)
    with open(path_to_file, 'r') as file:
        contents = file.read()
    return contents


def get_component_input(name):
    filenames = get_component(name)['files']

    files = {}
    for (fid, filename) in enumerate(filenames):
        files[filename] = _read_input_file(name, filename)

    return files
