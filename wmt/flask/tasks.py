import os

from flask import current_app

from .remote import remote


def exec_remote_wmt(host, uuid, which_wmt_exe='wmt-exe', username=None,
                    password=None, extra_args=None):
    extra_args = extra_args or []

    which_wmt_slave = os.path.join(os.path.dirname(which_wmt_exe), 'wmt-slave')

    cmd = ' '.join([which_wmt_exe, uuid,
                    '--server-url=%s' % current_app.config['WMT_SERVER_URL'],
                    '--with-wmt-slave=%s' % which_wmt_slave,
                    '--daemon'] + extra_args)

    cmd = 'df -h'
    return remote(host, cmd, username, password, prompt=False)
