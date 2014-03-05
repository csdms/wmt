import paramiko
import getpass
import os

from ..config import site


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

    sftp = ssh.open_sftp()

    return (ssh, sftp)


def make_wmt_prefix(ssh, remote_path):
    ssh.exec_command(' '.join(['mkdir', '-p', remote_path]))


def copy_launch_file(sftp, local_path, remote_path):
    filename = os.path.basename(local_path)
    try:
        sftp.put(local_path, os.path.join(remote_path, filename))
    except IOError as error:
        raise ValueError(' -> '.join([local_path, os.path.join(remote_path, filename)]))


def execute_launch_command(ssh, remote_path, uuid):
    cd_to_path = 'cd %s' % remote_path
    run_script = '/usr/bin/env -i /bin/bash ./launch.sh %s' % uuid

    stdin, stdout, stderr = ssh.exec_command(cd_to_path + ' && ' + run_script + ' &')

    return (stdin, stdout, stderr)


def get_host_launch_files(host):
    return [
        os.path.join(site['db'], 'hosts', host, 'files', 'launch.sh'),
        os.path.join(os.path.dirname(__file__), '..', 'scripts', 'launch.py'),
    ]


def launch_cmt_on_host(uuid, host, username, password=None, args=[]):
    try:
        (ssh, sftp) = open_connection_to_host(host, username, password=password)
    except paramiko.AuthenticationException:
        resp = {
            'status_code': 401,
            'stdout': '',
            'stderr': '',
        }
    else:
        remote_path = os.path.join('.wmt', uuid)

        make_wmt_prefix(ssh, remote_path)

        for file in get_host_launch_files(host):
            copy_launch_file(sftp, file, remote_path)

        (_, stdout, stderr) = execute_launch_command(ssh, remote_path, uuid)

        resp = {
            'status_code': 200,
            'stdout': ''.join(stdout.readlines()),
            'stderr': ''.join(stderr.readlines()),
        }

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
