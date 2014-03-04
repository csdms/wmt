import os
import shutil


def find_file(filename, paths=['.', ]):
    for path in paths:
        path_to_file = os.path.join(path, filename)
        if os.path.isfile(path_to_file):
            return path_to_file
    return None


def execute(env):
    this_dir = os.path.abspath(os.path.dirname(__file__))

    os.mkdir('HYDRO_IN')
    os.mkdir('HYDRO_OUTPUT')

    os.rename('hydrotrend.in', 'HYDRO.IN')
    shutil.move('HYDRO.IN', 'HYDRO_IN')

    src = find_file(env['hypsometry_file'])
    if src is not None:
        shutil.copy(src, os.path.join('HYDRO_IN', 'HYDRO0.HYPS'))
    else:
        raise ValueError(env['hypsometry_file'])

