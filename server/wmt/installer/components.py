import os
import urllib2
import zipfile
import shutil
import tempfile


def download_components(url, dest_dir='.'):
    dest = os.path.join(dest_dir, os.path.basename(url))
    src_fp = urllib2.urlopen(url)
    with open(dest, 'w') as dest_fp:
        dest_fp.write(src_fp.read())
    return dest


def unpack_components(filename, dest_dir='.'):
    zip = zipfile.ZipFile(filename, mode='r')
    files = zip.namelist()
    prefix = os.path.commonprefix(files)
    zip.extractall(dest_dir)

    return os.path.join(dest_dir, prefix)


def download_and_unpack(url, dest='.'):
    tmp_dir = tempfile.mkdtemp(prefix='wmt_')
    try:
        package = download_components(url, dest_dir=tmp_dir)
        base_dir = unpack_components(package, dest_dir=tmp_dir)
        move_directory(base_dir, dest)
    except Exception:
        raise
    finally:
        shutil.rmtree(tmp_dir)

    return dest


def download_and_unpack_from_github(repo, dest='.', file='master.zip'):
    url = 'https://github.com/{0}/archive/{1}'.format(repo, file)
    try:
        download_and_unpack(url, dest=dest)
    except urllib2.HTTPError:
        raise ValueError(url)


def _make_destination_directory(destdir):
    import errno
    try:
        if destdir.endswith(os.pathsep):
            os.makedirs(destdir)
        else:
            os.makedirs(os.path.dirname(destdir))
    except os.error as error:
        if error.errno == errno.EEXIST:
            pass
        else:
            raise


def move_directory(srcdir, destdir):
    """Move *srcdir* to *destdir* or inside of *destdir*. If *destdir*
    ends with a path separator, then *srcdir* will be moved inside of
    *destdir*, otherwise *srcdir* will become *destdir*.
    """
    _make_destination_directory(destdir)

    shutil.move(srcdir, destdir)

    return destdir


def main():
    import argparse
    parser = argparse.ArgumentParser()

    parser.add_argument('--dest', default='components',
                        help='unpack database here')

    args = parser.parse_args()

    download_and_unpack_from_github('csdms/component_metadata', dest=args.dest)


if __name__ == '__main__':
    main()
