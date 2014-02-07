import os

from .config import site
from .cca.json import load_component_from_json


def load_palette():
    palette = {}
    for file in os.listdir(site['data']):
        try:
            desc = load_component_from_json(
                os.path.join(site['data'], file))
        except ValueError:
            pass
        else:
            palette[desc['id']] = desc

    return palette


PALETTE = load_palette()
