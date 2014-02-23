import web
import os

from ..models import (models, users, submissions)
from ..render import render
from ..validators import (not_too_long, not_too_short, not_bad_json)
from ..cca import rc_from_json
from .. import run


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
        web.form.Button('Stage')
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

    def GET(self, id):
        submission = submissions.get_submission(int(id))
        form = self.form()
        form.fill(submission)
        return render.update(submission, form)

    def POST(self, id):
        form = self.form()
        submission = submissions.get_submission(id)
        if not form.validates():
            return render.update(submission, form)
        submissions.update(id, status=form.d.status, message=form.d.message)
        raise web.seeother('/run/show')


class Upload(object):
    def GET(self):
        return """<html><head></head><body>
<form method="POST" enctype="multipart/form-data" action="">
<input type="file" name="myfile"/>
<br/>
<input type="submit"/>
</form>
</body></html>
    """

    def POST(self):
        import uuid
        x = web.input(myfile={})
        filename = str(uuid.uuid4())
        dest = os.path.join('/data/ftp/pub/users/wmt/', x['myfile'].filename)
        #dest = os.path.join('/data/ftp/pub/users/wmt/', filename)
        with open(dest, 'w') as f:
            f.write(x['myfile'].value)

        raise web.seeother('/run/upload')


class Show(object):
    def GET(self):
        return render.status(submissions.get_submissions())
