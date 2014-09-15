import os
import urlparse
import urllib2
import tarfile
import subprocess


PICKUP_URL = 'http://csdms.colorado.edu/pub/users/wmt/'
CHUNK_SIZE_IN_BYTES = 10240

SERVER = 'http://csdms.colorado.edu/wmt'


class Error(Exception):
    pass


class UploadError(Error):
    def __init__(self, code, file):
        self._code = code
        self._file = file

    def __str__(self):
        return '%s: unable to upload (error %d)' % (self._file, self._code)


class ComponentRunError(Error):
    def __init__(self, msg):
        self._msg = msg

    def __str__(self):
        return self._msg


def get_filename_from_header(header):
    try:
        disposition = headers['content-disposition']
    except KeyError:
        raise

    for attr in disposition.split(';'):
        if attr.strip().startswith('filename'):
            dest_name = attr[attr.index('=') + 1:].strip()
            break

    return dest_name


def download_file(url):
    resp = requests.get(url, stream=True)

    try:
        dest_name = get_filename_from_header(resp.headers)
    except KeyError:
        dest_name = os.path.basename(url)

    with open(dest_name, 'wb') as fp:
        for chunk in resp.iter_content():
            if chunk: # filter out keep-alive new chunks
                fp.write(chunk)
                fp.flush()

    return dest_name


def download_run_tarball(uuid):
    import requests

    url = os.path.join(SERVER, 'run/download')
    resp = requests.post(url, stream=True,
                         data={
                             'uuid': uuid,
                             'filename': '',
                         })

    dest_name = uuid + '.tar.gz'
    with open(dest_name, 'wb') as fp:
        for chunk in resp.iter_content():
            if chunk: # filter out keep-alive new chunks
                fp.write(chunk)
                fp.flush()

    return dest_name


def update_run_status(uuid, status, message):
    import requests

    url = os.path.join(SERVER, 'run/update')
    resp = requests.post(url, data={
        'uuid': uuid,
        'status': status,
        'message': message,
    })

    return resp


def upload_run_tarball(uuid):
    import requests
    from requests_toolbelt import MultipartEncoder

    tarball = uuid + '.tar.gz'

    url = os.path.join('http://csdms.colorado.edu/wmt/run/upload')
    with open(tarball, 'r') as fp:
        m = MultipartEncoder(fields={'file': (tarball, fp, 'application/x-gzip')})
        resp = requests.post(url, data=m, headers={'Content-Type': m.content_type})

    if resp.status_code != 200:
        raise UploadError(resp.status_code, tarball)
    else:
        return resp


def download_chunks(url):
    try:
        resp = urllib2.urlopen(url)
    except urllib2.HTTPError:
        raise

    print resp.info().getheader('Transfer-Encoding')
    dest_name = os.path.basename(url)

    with open(dest_name, 'w') as dest_fp:
        while 1:
            chunk_size = int(resp.readline())
            if chunk_size == 0:
                break
            chunk = resp.read(chunk_size)
            dest_fp.write(chunk)
            resp.read(2)

    return os.path.abspath(dest_name)


def generate_error_message(name, error):
    try:
        with open('_%s.err' % name, 'r') as err:
            stderr = err.read()
    except IOError:
        stderr = ''

    return '\n'.join([str(error), stderr, ])


class WmtTask(object):
    def __init__(self, id):
        self._id = id
        self._wmt_dir = os.path.expanduser('~/.wmt')
        self._task_dir = os.path.join(self._wmt_dir, id)

    @property
    def id(self):
        return self._id

    @property
    def task_dir(self):
        return self._task_dir

    def setup(self):
        update_run_status(self.id, 'preparing', 'preparing for simulation')

        try:
            os.makedirs(self.task_dir)
        except os.error:
            pass

        os.chdir(self._wmt_dir)

        update_run_status(self.id, 'downloading', 'downloading simulation data')
        dest = download_run_tarball(self.id)

        update_run_status(self.id, 'unpacking', 'unpacking simulation data')
        with tarfile.open(dest) as tar:
            tar.extractall()

        os.chdir(self.task_dir)

        update_run_status(self.id, 'setup', 'setup complete')

    def run(self):
        update_run_status(self.id, 'running', 'running simulation')

        os.chdir(self.task_dir)

        for item in os.listdir('.'):
            if os.path.isdir(item):
                os.chdir(item)
                if os.path.isfile('run.sh'):
                    update_run_status(self.id, 'running', '%s: running simulation' % item)
                    (stdout, stderr) = (open('_%s.out' % item, 'w'), open('_%s.err' % item, 'w'))
                    try:
                        subprocess.check_call(['/bin/bash', 'run.sh'], stdout=stdout, stderr=stderr)
                    except subprocess.CalledProcessError as error:
                        raise ComponentRunError(generate_error_message(item, error))
                    finally:
                        stdout.close()
                        stderr.close()
                os.chdir('..')

        update_run_status(self.id, 'ran', 'finished simulation')


    def teardown(self):
        update_run_status(self.id, 'packaging', 'packing simulation output')
        os.chdir(self._wmt_dir)

        with tarfile.open(self.id + '.tar.gz', mode='w:gz') as tar:
            tar.add(self.id)

        update_run_status(self.id, 'uploading', 'uploading simulation')
        upload_run_tarball(self.id)
        update_run_status(self.id, 'uploaded', 'simulation is now available')


    def execute(self):
        self.setup()
        self.run()
        self.teardown()


def launch(id):
    task = WmtTask(id)
    task.execute()


def main():
    import traceback
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('id', help='Run ID')
    args = parser.parse_args()

    try:
        launch(args.id)
    except Error as error:
        update_run_status(args.id, 'error', str(error))
    except Exception as error:
        update_run_status(args.id, 'error', traceback.format_exc())


if __name__ == '__main__':
    main()
