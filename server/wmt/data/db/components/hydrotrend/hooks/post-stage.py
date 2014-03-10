import os
import shutil

from wmt.utils.hook import find_file


def execute(env):
    os.mkdir('HYDRO_IN')
    os.mkdir('HYDRO_OUTPUT')

    os.rename('hydrotrend.in', 'HYDRO.IN')
    shutil.move('HYDRO.IN', 'HYDRO_IN')

    try:
        src = find_simulation_file(env['hypsometry_file'])
    except FileNotFoundError:
        raise ValueError(env['hypsometry_file'])

    shutil.copy(src, os.path.join('HYDRO_IN', 'HYDRO0.HYPS'))

    if src is not None:
        shutil.copy(src, os.path.join('HYDRO_IN', 'HYDRO0.HYPS'))
    else:
        raise ValueError(env['hypsometry_file'])

