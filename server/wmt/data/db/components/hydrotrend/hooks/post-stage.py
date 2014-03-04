import os
import shutil


def execute(env):
    this_dir = os.path.abspath(os.path.dirname(__file__))

    os.mkdir('HYDRO_IN')
    os.mkdir('HYDRO_OUTPUT')

    os.rename('hydrotrend.in', 'HYDRO.IN')
    shutil.move('HYDRO.IN', 'HYDRO_IN')

    shutil.copy(
        os.path.join(this_dir, '..', 'files', env['hypsometry_file']),
        os.path.join('HYDRO_IN', 'HYDRO0.HYPS'))
