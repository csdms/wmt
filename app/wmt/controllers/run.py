import web

from ..models import (models, users)
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
