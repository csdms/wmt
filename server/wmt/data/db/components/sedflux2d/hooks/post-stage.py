import shutil

from wmt.utils.hook import find_simulation_input_file


def execute(env):
    if env['bathymetry_method'] == 'user':
        src = find_simulation_input_file(env['bathymetry_file'])
        shutil.copy(src, 'bathymetry.csv')

    if env['sea_level_method'] == 'user':
        src = find_simulation_input_file(env['sea_level_file'])
        shutil.copy(src, 'sea_level.csv')
