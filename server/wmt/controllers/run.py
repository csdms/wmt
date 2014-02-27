import web
import os
import json

from ..models import (models, users, submissions)
from ..render import render
from ..validators import (not_too_long, not_too_short, not_bad_json)
from ..cca import rc_from_json
from .. import run
from ..utils.io import chunk_copy
from ..config import logger


class StageIn(object):
    form = web.form.Form(
        web.form.Textbox('name',
                         not_too_short(3),
                         not_too_long(20),
                         size=30, description='Simulation name:'),
        web.form.Textarea('json',
                          not_bad_json,
                          rows=40, cols=80, description=None),
        web.form.Button('Stage')
    )

    def GET(self, id):
        try:
            model = models.get_model(int(id))
        except (ValueError, models.BadIdError):
            return render.stagein(self.form())
        else:
            self.form.fill(model)
            return render.stagein(self.form)

    def POST(self, id):
        form = self.form()
        if not form.validates():
            return render.stagein(form)

        run_id = run.stagein(id)
        dropoff_dir = run.stageout(run_id)

        return dropoff_dir


class New(object):
    form = web.form.Form(
        web.form.Textbox('name',
                         not_too_short(3),
                         not_too_long(20),
                         size=30, description='Simulation name:'),
        web.form.Button('Create')
    )

    def GET(self):
        return render.stagein(self.form)

    def POST(self):
        form = self.form()
        if not form.validates():
            return render.stagein(form)

        return submissions.new(form.d.name)


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
        submission = submissions.get_submission(uuid)
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


class Show(object):
    def GET(self):
        return render.status(submissions.get_submissions())
