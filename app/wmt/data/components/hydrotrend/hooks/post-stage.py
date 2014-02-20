import os
from wmt.config import site


class execute_in_dir(object):
    def __init__(self, dir):
        self._init_dir = os.getcwd()
        self._exe_dir = dir

    def __enter__(self):
        os.chdir(self._exe_dir)
        return os.getcwd()

    def __exit__(self, type, value, traceback):
        os.chdir(self._init_dir)
        return isinstance(value, OSError)


def execute(name, id, env):
    import shutil
    this_dir = os.path.abspath(os.path.dirname(__file__))
    stage_dir = os.path.join(site['stage'], id, name)

    with execute_in_dir(stage_dir) as _:
        os.mkdir('INPUT')
        os.mkdir('OUTPUT')

        os.rename('hydrotrend.in.tmpl', 'HYDRO.IN')
        shutil.move('HYDRO.IN', 'INPUT')

        shutil.copy(
            os.path.join(this_dir, '..', 'files', env['hypsometry_file']),
            os.path.join('INPUT', 'HYDRO0.HYPS'))
