import web
import os
import json

from ..models import (models, users, submissions)
from ..render import render
from ..validators import (not_too_long, not_too_short, not_bad_json,
                          valid_uuid, submission_exists)
from ..cca import rc_from_json
from .. import run
from ..utils.io import chunk_copy
from ..config import logger


class Launch(object):
    def POST(self, uuid):
        pass


class Stage(object):
    form = web.form.Form(
        web.form.Textbox('uuid',
                         valid_uuid,
                         submission_exists(),
                         size=80, description='Simulation id:'),
        web.form.Button('Stage')
    )

    def GET(self):
        return render.stagein(self.form)

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.stagein(form)

        submissions.update(form.d.uuid,
            status='staging',
            message='staging the model simulation...')

        submissions.stage(form.d.uuid)

        submissions.update(form.d.uuid,
            status='staged',
            message='ready for launch')
        raise web.seeother('/run/show')


class New(object):
    form = web.form.Form(
        web.form.Textbox('name',
                         not_too_short(3),
                         not_too_long(20),
                         size=30, description='Name:'),
        web.form.Textbox('description',
                         size=30, description='Description:'),
        web.form.Textbox('model_id',
                         size=30, description='Model id:'),
        web.form.Button('Create')
    )

    def GET(self):
        return render.stagein(self.form)

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.stagein(form)

        uuid = submissions.new(form.d.name, form.d.model_id)
        #run.stagein(uuid)

        return uuid


class Update(object):
    form = web.form.Form(
        web.form.Textbox('status',
                         not_too_short(3),
                         not_too_long(20),
                         size=30, description='status:'),
        web.form.Textbox('message',
                         not_too_long(256),
                         size=30, description='message:'),
        web.form.Button('Update')
    )

    def GET(self, uuid):
        try:
            submission = submissions.get_submission(uuid)
        except submissions.IdError:
            raise web.internalerror(render.error(
"""
The given sumbission UUID does not exist. Please see the status table for a
list of valid submissions.
"""
            ))

        form = self.form()
        form.fill(submission)
        return render.update(submission, form)

    def POST(self, uuid):
        form = self.form()
        submission = submissions.get_submission(uuid)
        if not form.validates():
            return render.update(submission, form)
        submissions.update(uuid, status=form.d.status, message=form.d.message)
        raise web.seeother('/run/show')


_UPLOAD_DIR = '/data/ftp/pub/users/wmt'
_CHUNK_SIZE = 8192

class Upload(object):
    def GET(self, uuid):
        return """<html><head></head><body>
<form method="POST" enctype="multipart/form-data" action="">
<input type="file" name="file"/>
<br/>
<input type="submit"/>
</form>
</body></html>"""

    def POST(self, uuid, filename):
        user_data = web.input(file={})

        path_to_dest = os.path.join(_UPLOAD_DIR, uuid, user_data['file'].filename)

        import hashlib
        checksum = hashlib.md5()

        with open(path_to_dest, 'w') as dest_fp:
            chunk_copy(user_data['file'].file, dest_fp,
                       chunk_size=_CHUNK_SIZE, checksum=checksum)

        return json.dumps({'checksum': checksum.hexdigest()})


class Download(object):
    def GET(self, uuid, filename):
        web.header("Content-Disposition", "attachment; filename=%s" % filename)
        web.header('Content-type', 'application/octet-stream')
        web.header('Transfer-encoding', 'chunked')

        path_to_file = os.path.join(_UPLOAD_DIR, uuid, filename)
        if not os.path.isfile(path_to_file):
            raise web.notfound()

        with open(path_to_file, 'r') as fp:
            while 1:
                chunk = fp.read(_CHUNK_SIZE)
                if not chunk:
                    break
                yield '%X\r\n%s\r\n' % (len(chunk), chunk)
        yield '%X\r\n%s\r\n' % (0, '')


class DownloadBundle(object):
    def GET(self, uuid):
        import tempfile
        import tarfile

        x = web.input(filename=uuid + '.tar.gz', format='gz')

        if x['format'] not in ['gz', 'bz2']:
            raise ValueError('%s: unknown format' % x['format'])

        web.header("Content-Disposition", "attachment; filename=%s" % x['filename'])
        web.header('Content-type', 'application/x-gzip')
        web.header('Transfer-encoding', 'chunked')

        os.chdir(_UPLOAD_DIR)

        if not os.path.isdir(uuid):
            raise web.notfound()

        with tempfile.TemporaryFile() as tmp:
            with tarfile.open(fileobj=tmp, mode='w:' + x['format']) as tar:
                tar.add(uuid)
            tmp.seek(0)

            while 1:
                chunk = tmp.read(_CHUNK_SIZE)
                if not chunk:
                    break
                yield '%X\r\n%s\r\n' % (len(chunk), chunk)
        yield '%X\r\n%s\r\n' % (0, '')


class Show(object):
    def GET(self):
        return render.status(submissions.get_submissions())
