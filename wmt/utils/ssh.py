from __future__ import absolute_import

import paramiko
import getpass
import os

from ..config import site
from ..models import submissions


def site_url():
    import urlparse
    parts = (site['scheme'], site['netloc'], site['path'], '', '')
    return urlparse.urlunsplit(parts)


def pickup_url():
    import urlparse
    parts = (site['pickup_scheme'], site['pickup_netloc'],
             site['pickup_path'], '', '')
    return urlparse.urlunsplit(parts)


def open_connection_to_host(host, username, password=None, onerror='raise'):
    assert(onerror in ['raise', 'prompt'])

    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    try:
        ssh.connect(host, username=username, password=password)
    except paramiko.AuthenticationException:
        if onerror == 'raise':
            raise
        else:
            ssh.connect(host, username=username, password=getpass.getpass())

    return ssh


def make_wmt_prefix(ssh, remote_path):
    ssh.exec_command(' '.join(['mkdir', '-p', remote_path]))


def copy_launch_file(sftp, local_path, remote_path):
    filename = os.path.basename(local_path)
    try:
        sftp.put(local_path, os.path.join(remote_path, filename))
    except IOError as error:
        raise
        raise ValueError(' -> '.join([local_path, os.path.join(remote_path, filename)]))


def execute_launch_command(ssh, wmt_prefix, uuid):
    # run_command = ' '.join([
    #     os.path.join(wmt_prefix, 'bin', 'wmt-exe'),
    #     #'/Users/huttone/anaconda/bin/wmt-exe',
    #     uuid,
    #     '--server-url=%s' % site_url(),
    #     '--with-wmt-slave=%s' % os.path.join(wmt_prefix, 'bin', 'wmt-slave'),
    #     '--daemon',
    # ])
    run_command = ' '.join([
        os.path.join(wmt_prefix, 'bin', 'wmt-script'),
        '--run',
        '--launcher=qsub',
        uuid
    ])
    #wmt_execute = os.path.join(wmt_prefix, 'bin', 'wmt-execute.sh')
    #wmt_execute = os.path.join(wmt_prefix, 'bin', 'wmt-qsub.sh')

    #run_command = '/usr/bin/env -i /bin/bash %s %s --server-url=%s' % (wmt_execute, uuid, site_url())
    submissions.update(uuid, status='launching', message='Running command: %s' % run_command)
    stdin, stdout, stderr = ssh.exec_command(run_command)

    return (stdin, stdout, stderr)


def get_host_wmt_prefix(host):
    import json
    path_to_info = os.path.join(site['db'], 'hosts', host, 'db', 'info.json')

    with open(path_to_info, 'r') as fp:
        info = json.loads(fp.read())

    return info['wmt_prefix']


def get_host_launch_files(host):
    return [
        os.path.join(site['db'], 'hosts', host, 'files', 'launch.sh'),
        os.path.join(os.path.dirname(__file__), '..', 'scripts', 'launch.py'),
    ]


def launch_cmt_on_host(uuid, host, username, password=None, args=[]):
    try:
        ssh = open_connection_to_host(host, username, password=password)
    except paramiko.AuthenticationException:
        resp = {
            'status_code': 401,
            'stdout': '',
            'stderr': '',
        }
    except Exception as error:
        resp = {
            'status_code': 500,
            'stdout': '',
            'stderr': str(error),
        }
    else:
        remote_path = os.path.join('.wmt', uuid)

        make_wmt_prefix(ssh, remote_path)

        prefix = get_host_wmt_prefix(host)
        (_, stdout, stderr) = execute_launch_command(ssh, prefix, uuid)

        resp = {
            'status_code': 200,
            'stdout': ''.join(stdout.readlines()),
            'stderr': ''.join(stderr.readlines()),
        }
        ssh.close()
    #finally:

    return resp


def launch_command_on_host(username, host, script, password='', args=[]):
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    try:
        ssh.connect(host, username=username, password=password)
    except paramiko.AuthenticationException:
        password = getpass.getpass()
        launch_command_on_host(username, host, script, password=password)
    except paramiko.SSHException:
        return 'Ssh error: ssh %s@%s:%s' % (username, host, password)

    sftp = ssh.open_sftp()

    pre_launch(ssh, sftp)


    stdin, stdout, stderr = ssh.exec_command(
        ' '.join(
            ['python', os.path.join('.', os.path.basename(script))] + args))

    return {
        'stdout': ''.join(stdout.readlines()),
        'stderr': ''.join(stderr.readlines()),
    }


if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('script', help='script to run on host')
    args = parser.parse_args()

    resp = launch_command_on_host('huttone', 'beach.colorado.edu', args.script)

    for stream in ['stdout', 'stderr']:
        print '%s:%s%s' % (stream, os.linesep, resp[stream])
