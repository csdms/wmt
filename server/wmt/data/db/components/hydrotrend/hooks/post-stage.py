import os
import shutil

from wmt.utils.hook import find_simulation_input_file

def execute(env):
    os.mkdir('HYDRO_IN')
    os.mkdir('HYDRO_OUTPUT')

    os.rename('hydrotrend.in', 'HYDRO.IN')
    shutil.move('HYDRO.IN', 'HYDRO_IN')

    src = find_simulation_input_file(env['hypsometry_file'])

    shutil.copy(src, os.path.join('HYDRO_IN', 'HYDRO0.HYPS'))
