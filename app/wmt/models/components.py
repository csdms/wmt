import os
import json

from ..config import palette, site


class Error(Exception):
    pass


class IdError(Error):
    def __init__(self, id):
        self._id = id

    def __str__(self):
        return str(self._id)


def get_components():
    return palette.values()


def get_component_names(sort=False):
    names = list(palette.keys())
    if sort:
        names.sort()
    return names


def get_component(name):
    try:
        return palette[name]
    except KeyError:
        raise IdError(name)


def get_component_params(name):
    comp = get_component(name)
    return comp['parameters']


def get_component_defaults(name):
    defaults = {}

    comp = get_component(name)
    for parameter in comp['parameters']:
        defaults[parameter['key']] = parameter['value']['default']

    return defaults


def _read_input_file(filename):
    input_file_dir = os.path.join(site['data'], 'components')
    path_to_file = os.path.join(input_file_dir, filename)
    with open(path_to_file, 'r') as file:
        contents = file.read()
    return contents


def _decorate_with_header(contents, **kwds):
    header = []
    for item in kwds.items():
        header.append('%s: %s' % item)
    header_width = max(len(line) for line in header)

    return os.linesep.join(
        header + [
            '-' * header_width,
            contents,
            '-' * header_width,
        ])


def get_component_input(name):
    input_files = get_component(name)['input_files']

    lines = []
    for (fid, filename) in enumerate(input_files):
        contents = _read_input_file(filename)
        lines.append(
            _decorate_with_header(
                contents, file=filename,
                fileno='%d of %d' % (fid, len(input_files))))

    return os.linesep.join(lines)


def get_component_formatted_input(name, **kwds):
    from ..utils.templatefile import FileFormatter

    format = FileFormatter(get_component_defaults(name))

    return format.format(get_component_input(name), **kwds)
