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
from ..config import logger, site


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

        submissions.launch(form.d.uuid, form.d.username, form.d.host,
                           password=form.d.password)

        submissions.update(form.d.uuid,
            status='launched',
            message='simulation has launched')
        raise web.seeother('/run/show')


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
        return render.titled_form('Create Simulation', self.form())

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.titled_form('Create Simulation', form)

        uuid = submissions.new(form.d.name, form.d.model_id)

        return uuid


class Update(object):
    form = web.form.Form(
        web.form.Textbox('uuid',
                         valid_uuid,
                         submission_exists(),
                         size=80, description='Simulation id:'),
        web.form.Textbox('status',
                         not_too_short(3),
                         not_too_long(20),
                         size=80, description='status:'),
        web.form.Textbox('message',
                         not_too_long(256),
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
                         not_too_long(256),
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


class Show(object):
    def GET(self):
        return render.status(submissions.get_submissions())
