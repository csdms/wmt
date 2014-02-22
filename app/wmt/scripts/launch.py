import os
import urlparse
import urllib2
import tarfile


PICKUP_URL = 'http://csdms.colorado.edu/pub/users/wmt/'
CHUNK_SIZE_IN_BYTES = 10240


def download_large_file(url):
    resp = urllib2.urlopen(url)
    total_size = int(resp.info().getheader('Content-Length').strip())

    dest_name = os.path.basename(url)

    with open(dest_name, 'w') as dest_fp:
        while 1:
            chunk = resp.read(CHUNK_SIZE_IN_BYTES)
            if not chunk:
                break
            dest_fp.write(chunk)

    return os.path.abspath(dest_name)


def setup(id):
    launch_dir = os.path.join('.wmt', id)
    try:
        os.makedirs(launch_dir)
    except os.error:
        raise

    os.chdir(launch_dir)

    dest = download_large_file(
        os.path.join(PICKUP_URL, id, 'wmt_simulation.tar.gz'))

    with tarfile.open(dest) as tar:
        tar.extractall()


def run(id):
    pass


def teardown(id):
    pass


def launch(id):
    setup(id)
    run(id)
    teardown(id)


def main():
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument('id', help='Run ID')
    args = parser.parse_args()

    launch(args.id)


if __name__ == '__main__':
    main()
