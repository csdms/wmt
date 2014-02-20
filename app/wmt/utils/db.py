import os

from .json import load_component


def load_palette(palette_dir):

    palette = {}
    for item in os.listdir(palette_dir):
        if os.path.isdir(os.path.join(palette_dir, item)):
            path_to_json = os.path.join(palette_dir, item, item + '.json')
            try:
                desc = load_component(path_to_json)
            except ValueError:
                logger.error('%s: unable to load component' % path_to_json)
            else:
                palette[desc['id']] = desc

    return palette
