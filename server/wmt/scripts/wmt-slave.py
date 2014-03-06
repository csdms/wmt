import os
import argparse
import urlparse
import urllib2
import tarfile
import subprocess
import shutil


class Error(Exception):
    pass


class UploadError(Error):
    def __init__(self, code, file):
        self._code = code
        self._file = file

    def __str__(self):
        return '%s: unable to upload (error %d)' % (self._file, self._code)


class DownloadError(UploadError):
    def __str__(self):
        return '%s: unable to download (error %d)' % (self._file, self._code)


class ComponentRunError(Error):
    def __init__(self, msg):
        self._msg = msg

    def __str__(self):
        return self._msg


def download_run_tarball(server, uuid, dir='.'):
    import requests

    url = os.path.join(server, 'run/download')
    resp = requests.post(url, stream=True,
                         data={
                             'uuid': uuid,
                             'filename': '',
                         })

    if resp.status_code == 200:
        dest_name = os.path.join(dir, uuid + '.tar.gz')
        with open(dest_name, 'wb') as fp:
            for chunk in resp.iter_content():
                if chunk: # filter out keep-alive new chunks
                    fp.write(chunk)
                    fp.flush()
    else:
        raise DownloadError(resp.status_code, tarball)

    return dest_name


def update_run_status(server, uuid, status, message):
    import requests

    url = os.path.join(server, 'run/update')
    resp = requests.post(url, data={
        'uuid': uuid,
        'status': status,
        'message': message,
    })

    return resp


def upload_run_tarball(server, tarball):
    import requests
    from requests_toolbelt import MultipartEncoder

    url = os.path.join(server, 'run/upload')
    with open(tarball, 'r') as fp:
        m = MultipartEncoder(fields={
            'file': (tarball, fp, 'application/x-gzip')})
        resp = requests.post(url, data=m,
                             headers={'Content-Type': m.content_type})

    if resp.status_code != 200:
        raise UploadError(resp.status_code, tarball)
    else:
        return resp


def generate_error_message(name, error):
    try:
        with open('_%s.err' % name, 'r') as err:
            stderr = err.read()
    except IOError:
        stderr = ''

    return '\n'.join([str(error), stderr, ])


def create_user_execution_dir(id, prefix='~/.wmt'):
    path = os.path.join(prefix, id)

    try:
        os.makedirs(path)
    except os.error:
        if os.path.isdir(path):
            pass
        else:
            raise

    return path


def dir_contains_run_script(path):
    return os.path.isfile(os.path.join(path, 'run.sh'))


def components_to_run(path):
    components = {}

    for item in os.listdir(path):
        if dir_contains_run_script(os.path.join(path, item)):
            components[item] = os.path.abspath(os.path.join(path, item))

    return components


def run_component(name, **kwds):
    try:
        subprocess.check_call(['/bin/bash', 'run.sh'], **kwds)
    except subprocess.CalledProcessError as error:
        raise ComponentRunError(generate_error_message(name, error))


class open_logs(object):
    def __init__(self, name, dir='.'):
        prefix = os.path.abspath(dir)
        self._out_log = os.path.join(prefix, '_%s.out' % name)
        self._err_log = os.path.join(prefix, '_%s.err' % name)

    def __enter__(self):
        (self._out, self._err) = (open(self._out_log, 'w'),
                                  open(self._err_log, 'w'))
        return (self._out, self._err)

    def __exit__(self, type, value, traceback):
        self._out.close()
        self._err.close()
        return True


class WmtSlave(object):
    def __init__(self, id, server, env=None, dir='~/.wmt'):
        self._id = id
        self._wmt_dir = os.path.expanduser('~/.wmt')
        self._sim_dir = create_user_execution_dir(id, prefix=self._wmt_dir)
        self._server = server
        self._env = env


    @property
    def id(self):
        return self._id

    @property
    def sim_dir(self):
        return self._sim_dir

    def setup(self):
        self.update_status('downloading', 'downloading simulation data')
        dest = self.download_tarball(dir=self._wmt_dir)

        self.update_status('unpacking', 'unpacking simulation data')
        self.unpack_tarball(dest)

    def run(self):
        for (component, path) in components_to_run(self.sim_dir).items():
            self.update_status('running', 'running component: %s' % component)
            self.run_component(component, dir=path)

    def teardown(self):
        self.update_status('packing', 'packing simulation output')
        tarball = self.pack_tarball()

        self.update_status('uploading', 'uploading simulation output')
        self.upload_tarball(tarball)

        self.update_status('cleaning', 'cleaning up')
        self.cleanup()

    def execute(self):
        self.setup()
        self.run()
        self.teardown()

    def cleanup(self):
        shutil.rmtree(self._sim_dir, ignore_errors=True)
        tarball = os.path.join(self._wmt_dir, self.id + '.tar.gz')
        os.remove(tarball)

    def update_status(self, status, message):
        update_run_status(self._server, self.id, status, message)

    def run_component(self, name, dir='.'):
        with open_logs(name, dir=dir) as (stdout, stderr):
            run_component(stdout=stdout, stderr=stderr, env=self._env, cwd=dir)

    def download_tarball(self, dir='.'):
        ans = download_run_tarball(self._server, self.id, dir=dir)
        return ans

    def unpack_tarball(self, path):
        with tarfile.open(path) as tar:
            tar.extractall(path=self._wmt_dir)

    def pack_tarball(self):
        tarball = os.path.join(self._wmt_dir, self.id + '.tar.gz')
        with tarfile.open(tarball, mode='w:gz') as tar:
            tar.add(self._sim_dir)
        return tarball

    def upload_tarball(self, path):
        ans = upload_run_tarball(self._server, path)
        return ans


def launch(id, url, dir='~/.wmt'):
    env = {
        'PATH': os.pathsep.join([
            '/home/csdms/wmt/internal/Canopy_64bit/User',
            '/home/csdms/wmt/internal/bin',
            '/bin',
            '/usr/bin',
        ]),
        'LD_LIBRARY_PATH': 'home/csdms/wmt/internal/lib',
    }

    task = WmtSlave(id, url, env=env, dir=dir)
    task.execute()


class EnsureHttps(argparse.Action):
    def __call__(self, parser, namespace, values, option_string=None):
        import urlparse
        o = urlparse.urlsplit(values)
        url = urlparse.urlunsplit(('https', o.netloc, o.path, '', ''))
        setattr(namespace, self.dest, url)


def main():
    import traceback

    parser = argparse.ArgumentParser()
    parser.add_argument('id', help='run ID')
    parser.add_argument('--server-url',
                        default='https://csdms.colorado.edu/wmt',
                        action=EnsureHttps, help='URL of WMT server')
    parser.add_argument('--exec-dir', default=os.path.expanduser('~/.wmt'),
                        help='path to execution directory')

    args = parser.parse_args()

    try:
        launch(args.id, args.server_url, dir=args.exec_dir)
    except Error as error:
        update_run_status(args.server_url, args.id, 'error', str(error))
    except Exception as error:
        update_run_status(args.server_url, args.id, 'error',
                          traceback.format_exc())
    else:
        update_run_status(args.server_url, args.id, 'success',
                          'simulation is complete')


if __name__ == '__main__':
    main()
