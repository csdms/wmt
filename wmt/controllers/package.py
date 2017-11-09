import os
import shutil
import subprocess
import json
import web

from ..render import render
from ..validators import not_too_long, valid_uuid, submission_exists
from ..config import site
from ..utils.ssh import pickup_url


class Create(object):

    error_message = """ Unable to create tarball. This is either a bad
simulation UUID or the simulation has not yet been staged.
"""
    form = web.form.Form(
        web.form.Textbox('uuid',
                         valid_uuid,
                         submission_exists(),
                         size=80,
                         description='Simulation id:'),
        web.form.Textbox('filename',
                         not_too_long(512),
                         size=80,
                         description='Filename:'),
        web.form.Button('Create')
    )

    def GET(self):
        return render.titled_form('Create Tarball', self.form())

    def POST(self):
        form = self.form()
        if not form.validates():
            raise web.internalerror(self.error_message)

        filename = form.d.filename
        if len(filename) == 0:
            filename = form.d.uuid

        if not filename.endswith('.tar.gz'):
            filename += '.tar.gz'

        os.chdir(site['downloads'])

        if not os.path.isdir(form.d.uuid):
            raise web.internalerror(self.error_message)

        try:
            subprocess.check_call(['tar', '-zcf', filename, form.d.uuid])
        except subprocess.CalledProcessError:
            raise web.internalerror('Unable to create tarball.')

        try:
            shutil.copy(filename, site['pickup'])
        except:
            raise web.internalerror('Unable to copy tarball to pickup site.')
        else:
            os.remove(filename)

        web.header('Content-Type', 'application/json; charset=utf-8')
        return json.dumps({
            'uuid': form.d.uuid,
            'filename': filename,
            'url': pickup_url(),
        }, indent=2)

