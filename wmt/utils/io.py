from __future__ import print_function

import os

import requests
from requests_toolbelt import MultipartEncoder
import yaml

from distutils.dir_util import mkpath

# class execute_in_dir(object):
#     def __init__(self, dir):
#         self._init_dir = os.getcwd()
#         self._exe_dir = dir

#     def __enter__(self):
#         os.chdir(self._exe_dir)
#         return os.getcwd()

#     def __exit__(self, type, value, traceback):
#         os.chdir(self._init_dir)
#         return isinstance(value, OSError)


class execute_in_dir(object):
    def __init__(self, dir):
        self._dir = dir

    def __enter__(self):
        self._starting_dir = os.path.abspath(os.getcwd())
        if not os.path.isdir(self._dir):
            mkpath(self._dir)
        os.chdir(self._dir)
        return os.path.abspath(os.getcwd())

    def __exit__(self, type, value, traceback):
        os.chdir(self._starting_dir)
        return isinstance(value, OSError)


def write_key_value_pairs(filelike, **kwds):
    for item in kwds.items():
        print('%s: %s' % item, file=filelike, end=os.linesep)


def write_readme(prefix, mode='w', params={}):
    if mode not in ('a', 'w'):
        raise ValueError('{mode}: mode not understood'.format(mode=mode))

    path_to_readme = os.path.join(prefix, 'README.yaml')
    if mode == 'a' and os.path.isfile(path_to_readme):
        with open(path_to_readme, 'r') as fp:
            current = yaml.load(fp)
    else:
        current = dict()

    current.update(params)
    with open(path_to_readme, 'w') as fp:
        yaml.dump(current, stream=fp, default_flow_style=False)


def _copy_a_chunk(src_fp, dest_fp, chunk_size, checksum=None):
    chunk = src_fp.read(chunk_size)
    if not chunk or len(chunk) == 0:
        raise EOFError
    dest_fp.write(chunk)
    try:
        checksum.update(chunk)
    except AttributeError:
        pass


def chunk_copy(src_fp, dest_fp, chunk_size=8192):
    import hashlib
    checksum = hashlib.md5()

    while 1:
        try:
            _copy_a_chunk(src_fp, dest_fp, chunk_size, checksum=checksum)
        except EOFError:
            break

    return checksum


def upload_large_file(filename, url):
    with open(filename, 'r') as fp:
        m = MultipartEncoder(fields={'file': (filename, fp, 'application/octet-stream')})
        resp = requests.post(url, data=m, headers={'Content-Type': m.content_type})

    return resp


def upload_large_file_to_stage(filename, url, uuid):
    with open(filename, 'r') as fp:
        m = MultipartEncoder(fields={'file': (filename, fp, 'application/octet-stream'), 'uuid': uuid})
        resp = requests.post(url, data=m, headers={'Content-Type': m.content_type})

    return resp


def download_file(url):
    resp = requests.get(url, stream=True)

    try:
        for attr in resp.headers['content-disposition'].split(';'):
            if attr.strip().startswith('filename'):
                dest_name = attr[attr.index('=') + 1:].strip()
    except KeyError:
        dest_name = os.path.basename(url)

    with open(dest_name, 'wb') as fp:
        for chunk in resp.iter_content():
            if chunk: # filter out keep-alive new chunks
                fp.write(chunk)
                fp.flush()

    return dest_name
