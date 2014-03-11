import shutil

from wmt.utils.hook import find_simulation_input_file


def execute(env):
    if env['sea_level_method'] == 'user':
        src = find_simulation_input_file(env['sea_level_file'])
        shutil.copy(src, 'sea_level.csv')
