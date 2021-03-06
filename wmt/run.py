from __future__ import print_function

import os
import json
import shutil
import datetime

from .models import (models, components, submissions)
from .config import (site, logger)
from .utils.io import write_readme


class Simulation(object):
    def __init__(self, uuid):
        self._uuid = str(uuid)
        self._stage_dir = os.path.join(site['downloads'], self.uuid)

    @property
    def uuid(self):
        """The unique identifier of this simulation"""
        return self._uuid

    @property
    def stage_dir(self):
        """Where the simulation will be staged"""
        return self._stage_dir

    @property
    def model(self):
        """A dictionary representation of the model to be executed"""
        info = submissions.get_submission(uuid)
        return json.loads(models.get_model(info.model_id).json)

    @property
    def components(self):
        """A list of dictionary representations of the model components"""
        return self.model['model']

    def stage(self):
        """Stage the simulation by creating a stage folder for the simulation,
        component sub-folders, and a README.
        """
        self._setup_stage_dir()

        self._write_readme(mode='w', params={
            'user': 'anonymous',
            'staged_on': current_time_as_string()
        })

        for component in self.components:
            stage_component(self.stage_dir, component)

    def launch(self, host, username, password):
        """Send the simulation somewhere for it to  run.
        """
        launch_command_on_server(username, host, script, password=password)

    def teardown(self):
        """After simulation has returned, clean things up.
        """
        pass

    def _setup_stage_dir(self):
        try:
            os.mkdir(self.stage_dir)
        except OSError:
            logger.warning('%s: Stage directory already exists' % self.stage_dir)

        self._write_readme(mode='w', params={
            'user': 'anonymous',
            'start': current_time_as_string()
        })

    def _write_readme(self, mode='w', params={}):
        write_readme(os.path.join(self.stage_dir, 'README'), mode=mode,
                     params=params)


def stageout(uuid):
    run_dir = os.path.join(site['downloads'], uuid)
    dropoff_dir = os.path.join('/data/ftp/pub/users/wmt', uuid)

    write_to_readme(run_dir, 'a', stop=current_time_as_string())
    shutil.move(run_dir, dropoff_dir)

    return dropoff_dir
