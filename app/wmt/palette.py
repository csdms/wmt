import os

from .config import site
from .cca.json import load_component_from_json


def load_palette():
    palette_dir = os.path.join(site['data'], 'components')
    palette = {}
    for file in os.listdir(palette_dir):
        try:
            desc = load_component_from_json(
                os.path.join(palette_dir, file))
        except ValueError:
            pass
        else:
            palette[desc['id']] = desc

    return palette


PALETTE = load_palette()
