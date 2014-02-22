import os
import shutil


def execute(env):
    this_dir = os.path.abspath(os.path.dirname(__file__))

    os.mkdir('INPUT')
    os.mkdir('OUTPUT')

    os.rename('hydrotrend.in.tmpl', 'HYDRO.IN')
    shutil.move('HYDRO.IN', 'INPUT')

    shutil.copy(
        os.path.join(this_dir, '..', 'files', env['hypsometry_file']),
        os.path.join('INPUT', 'HYDRO0.HYPS'))
