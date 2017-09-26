import web
import os
import shutil
import json

from ..models import (models, users, submissions)
from ..render import render
from ..validators import (not_too_long, not_too_short, not_bad_json,
                          valid_uuid, submission_exists, model_exists)
from ..cca import rc_from_json
from ..utils.io import chunk_copy
from ..utils.ssh import pickup_url
from ..config import logger, site
from ..utils.ssh import launch_cmt_on_host

import threading


indent = 2


def launch_simulation(uuid, username, host, password):
    try:
        #resp = submissions.launch(uuid, username, host, password=password)
        resp = launch_cmt_on_host(uuid, host, username, password=password)
    except Exception as error:
        submissions.update(
            uuid,
            status='error',
            message='unexpected error launching simulation on %s (%s)' % (host, error))

    if resp['status_code'] == 200:
        return
    elif resp['status_code'] == 401:
        submissions.update(uuid,
            status='error', message='authentication error of %s' % host)
    else:
        submissions.update(
            uuid,
            status='error',
            message='unexpected error launching simulation on %s (%d: %s)' % (host, resp['status_code'], resp['stderr']))


def delete_tarball(uuid):
    os.chdir(site['pickup'])
    tarball = uuid + '.tar.gz'
    try:
        os.remove(tarball)
    except OSError:
        pass


def delete_results(uuid):
    os.chdir(site['pickup'])
    try:
        shutil.rmtree(uuid)
    except OSError:
        pass


def parse_submission_status(status):
    import yaml

    status.stdout = status.message
    status.time = ''
    status.time_elapsed = ''

    try:
        info = yaml.load(status.message)
    except yaml.YAMLError:
        info = {}

    if isinstance(info, dict):
        status.stdout = info.get('stdout', '')
        status.time = info.get('time', '')
        status.time_elapsed = info.get('time_elapsed', '')

    return status


class Launch(object):
    form = web.form.Form(
        web.form.Textbox('uuid',
                         valid_uuid,
                         submission_exists(),
                         size=80, description='Simulation id:'),
        web.form.Textbox('host',
                         not_too_long(512),
                         size=30, description='host:'),
        web.form.Textbox('username',
                         not_too_long(512),
                         size=30, description='username:'),
        web.form.Password('password',
                          web.form.notnull,
                          size=30,
                          description='password:'),
        web.form.Button('Launch')
    )
    def GET(self):
        return render.titled_form('Launch Simulation', self.form())

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.titled_form('Launch Simulation', form)

        submissions.update(form.d.uuid,
            status='launching',
            message='launching the model simulation...')

        args = (form.d.uuid, form.d.username, form.d.host, form.d.password)
        thread = threading.Thread(target=launch_simulation, args=args)
        thread.start()

        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps({
            'uuid': form.d.uuid,
            'username': form.d.username,
            'host': form.d.host,
        })


class Stage(object):
    form = web.form.Form(
        web.form.Textbox('uuid',
                         valid_uuid,
                         submission_exists(),
                         size=80, description='Simulation id:'),
        web.form.Button('Stage')
    )

    def GET(self):
        return render.titled_form('Stage Simulation', self.form())

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.titled_form('Stage Simulation', form)

        submissions.update(form.d.uuid,
            status='staging',
            message='staging the model simulation...')

        try:
            submissions.stage(form.d.uuid)
        except Exception as error:
            import traceback
            submissions.update(form.d.uuid, status='error',
                               message=traceback.format_exc())
            raise web.internalerror("Error staging simulation: %s" %
                                    traceback.format_exc())

        submissions.update(form.d.uuid,
            status='staged',
            message='ready for launch')


class New(object):
    form = web.form.Form(
        web.form.Textbox('name',
                         not_too_short(3),
                         not_too_long(512),
                         size=30, description='Name:'),
        web.form.Textbox('description',
                         size=30, description='Description:'),
        web.form.Textbox('model_id',
                         model_exists(),
                         size=30, description='Model id:'),
        web.form.Button('Create')
    )

    def GET(self):
        return render.titled_form('Create Simulation', self.form())

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.titled_form('Create Simulation', form)

        uuid = submissions.new(form.d.name, form.d.model_id)

        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps(uuid)


class UiDelete(object):
    form = web.form.Form(
        #web.form.Textbox('uuid',
        #                 valid_uuid,
        #                 submission_exists(),
        #                 size=80, description='Simulation ID:'),
        web.form.Button('Delete')
    )
    def GET(self, uuid):
        form = self.form()
        #form.fill(uuid=uuid)
        return render.confirm_delete(form)

    def POST(self, uuid):
        submissions.delete(uuid)
        delete_tarball(uuid)
        delete_results(uuid)
        raise web.seeother('/run/show')


class Delete(object):
    def POST(self, uuid):
        submissions.delete(uuid)
        raise web.seeother('/run/show')


class Update(object):
    form = web.form.Form(
        web.form.Textbox('uuid',
                         valid_uuid,
                         submission_exists(),
                         size=80, description='Simulation id:'),
        web.form.Textbox('status',
                         not_too_short(3),
                         not_too_long(512),
                         size=80, description='status:'),
        web.form.Textbox('message',
                         not_too_long(10240),
                         size=80, description='message:'),
        web.form.Button('Update')
    )

    def GET(self):
        return render.titled_form('Update Simulation', self.form())

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.titled_form('Update Simulation', form)

        submission = submissions.get_submission(form.d.uuid)
        submissions.update(form.d.uuid, status=form.d.status,
                           message=form.d.message)
        raise web.seeother('/run/show')


#_UPLOAD_DIR = '/data/ftp/pub/users/wmt'
#_UPLOAD_DIR = '/Library/WebServer/Documents/files'
_UPLOAD_DIR = site['pickup']
_CHUNK_SIZE = 8192

class Upload(object):
    def GET(self):
        return render.uploadform("uuid")

    def POST(self):
        user_data = web.input(file={}, uuid=None, filename=None)

        if user_data['filename'] is None:
            filename = user_data['file'].filename
        else:
            filename = user_data['filename']

        if user_data['uuid'] is None:
            path_to_dest = os.path.join(_UPLOAD_DIR, os.path.basename(filename))
        else:
            path_to_dest = os.path.join(_UPLOAD_DIR, user_data['uuid'], filename)

        with open(path_to_dest, 'w') as dest_fp:
            checksum = chunk_copy(user_data['file'].file, dest_fp,
                                  chunk_size=_CHUNK_SIZE)

        return json.dumps({
            'checksum': checksum.hexdigest(),
            'url': 'http://csdms.colorado.edu/pub/users/wmt/' + os.path.basename(path_to_dest)})


class UploadUuid(object):
    def GET(self):
        return render.uploadform("uuid")

    def POST(self, uuid):
        #user_data = web.input(file={}, uuid=None, filename=None)
        user_data = web.input(file={})

        #if user_data['filename'] is None:
        #    filename = user_data['file'].filename
        #else:
        #    filename = user_data['filename']
        filename = user_data['file'].filename

        #if user_data['uuid'] is None:
        #    path_to_dest = os.path.join(_UPLOAD_DIR, os.path.basename(filename))
        #else:
        #    path_to_dest = os.path.join(_UPLOAD_DIR, user_data['uuid'], filename)
        #path_to_dest = os.path.join(_UPLOAD_DIR, uuid, filename)
        path_to_dest = os.path.join(_UPLOAD_DIR, filename)

        with open(path_to_dest, 'w') as dest_fp:
            checksum = chunk_copy(user_data['file'].file, dest_fp,
                                  chunk_size=_CHUNK_SIZE)

        return json.dumps({
            'checksum': checksum.hexdigest(),
            'url': 'http://csdms.colorado.edu/pub/users/wmt/' + os.path.basename(path_to_dest)})


class Download(object):
    def GET(self, uuid, filename):
        web.header("Content-Disposition", "attachment; filename=%s" % filename)
        web.header('Content-type', 'application/octet-stream')
        web.header('Transfer-encoding', 'chunked')

        path_to_file = os.path.join(site['downloads'], uuid, filename)
        if not os.path.isfile(path_to_file):
            raise web.internalerror(
"""Unable to download simulation. This is either a bad simulation UUID or
the simulation has not yet been staged.
""")

        with open(path_to_file, 'r') as fp:
            while 1:
                chunk = fp.read(_CHUNK_SIZE)
                if not chunk:
                    break
                yield '%X\r\n%s\r\n' % (len(chunk), chunk)
        yield '%X\r\n%s\r\n' % (0, '')


class DownloadBundle(object):
    form = web.form.Form(
        web.form.Textbox('uuid',
                         valid_uuid,
                         submission_exists(),
                         size=80, description='Simulation id:'),
        web.form.Textbox('filename',
                         not_too_long(512),
                         size=80, description='filename:'),
        web.form.Button('Download')
    )
    def GET(self):
        return render.titled_form('Download Tarball', self.form())

    def POST(self):
        import tempfile
        import tarfile

        form = self.form()
        if not form.validates():
            raise web.internalerror(
"""Unable to download simulation. This is either a bad simulation UUID or
the simulation has not yet been staged.
""")

        filename = form.d.filename
        if len(filename) == 0:
            filename = form.d.uuid

        if not filename.endswith('.tar.gz'):
            filename = filename + '.tar.gz'

        format = 'gz'
        if format not in ['gz', 'bz2']:
            raise ValueError('%s: unknown format' % x['format'])

        web.header("Content-Disposition", "attachment; filename=%s" % filename)
        web.header('Content-type', 'application/x-gzip')
        web.header('Transfer-encoding', 'chunked')

        os.chdir(site['downloads'])

        if not os.path.isdir(form.d.uuid):
            raise web.internalerror(
"""Unable to download simulation. This is either a bad simulation UUID or
the simulation has not yet been staged.
""")

        with tempfile.TemporaryFile() as tmp:
            with tarfile.open(fileobj=tmp, mode='w:' + format) as tar:
                tar.add(form.d.uuid)
            tmp.seek(0)

            while 1:
                chunk = tmp.read(_CHUNK_SIZE)
                if not chunk:
                    break
                yield '%X\r\n%s\r\n' % (len(chunk), chunk)
        yield '%X\r\n%s\r\n' % (0, '')


class Get(object):
    def GET(self, uuid):
        try:
            info = submissions.get_submission(uuid)
        except submissions.IdError:
            raise web.internalerror("Unable to find submission %s" % uuid)

        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps(dict(info), indent=indent)


class GetAll(object):
    def GET(self):
        infos = submissions.get_submissions()

        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps([dict(info) for info in infos], indent=indent)


class Status(object):
    def GET(self, uuid):
        try:
            status = submissions.get_status(uuid)
        except submissions.IdError:
            raise web.internalerror("Unable to find submission %s" % uuid)

        return render.status(uuid, parse_submission_status(status))


class Show(object):
    def GET(self):
        statuses = [
            parse_submission_status(s)
            for s in submissions.get_submissions()
        ]
        return render.statustable(statuses)
        # return render.statustable(submissions.get_submissions())


class Visualize(object):

    form = web.form.Form(
        web.form.Textbox('uuid',
                         valid_uuid,
                         submission_exists(),
                         size=80, description='Simulation id:'),
        web.form.Textbox('component',
                         not_too_long(512),
                         size=80, description='Component:'),
        web.form.Textbox('filepath',
                         not_too_long(512),
                         size=80, description='Filepath:'),
        web.form.Button('Visualize')
    )

    error_message = """Unable to visualize simulation results. This is
either a bad simulation UUID or the simulation has not yet been
staged."""

    def GET(self):
        return render.titled_form('Visualize Simulation Output', self.form())

    def POST(self):
        import tarfile

        form = self.form()
        if not form.validates():
            raise web.internalerror(self.error_message)

        if not os.path.isdir(os.path.join(site['pickup'], form.d.uuid)):
            tarball = form.d.uuid + '.tar.gz'
            os.chdir(site['pickup'])
            with tarfile.open(tarball) as tar:
                tar.extractall()

        visualize_url = os.path.join(pickup_url(),
                                     form.d.uuid,
                                     form.d.component,
                                     form.d.filepath)
        web.seeother(visualize_url)


class VisualizeFile(object):

    error_message = """Unable to visualize simulation results. This is
either because the simulation has not completed, or the simulation
contains no visualization."""

    def GET(self, uuid):
        import tarfile

        info = submissions.get_submission(uuid)
        driver = models.get_model_driver(info['model_id'])

        tarball = uuid + '.tar.gz'
        os.chdir(site['pickup'])
        try:
            with tarfile.open(tarball) as tar:
                tar.extractall()
        except IOError:
            pass
        except:
            raise web.internalerror(self.error_message)

        try:
            driver['parameters']['_visualization']
        except KeyError:
            visualize_url = os.path.join(pickup_url(),
                                         uuid,
                                         driver['id'])
        else:
            visualize_url = os.path.join(pickup_url(),
                                         uuid,
                                         driver['id'],
                                         driver['parameters']['_visualization'])

        web.seeother(visualize_url)
