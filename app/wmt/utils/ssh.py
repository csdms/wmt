import paramiko
import getpass
import os


def launch_command_on_host(username, host, script, password=''):
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
    sftp.put(script, os.path.basename(script))

    stdin, stdout, stderr = ssh.exec_command(
        ' '.join(['python',
                  os.path.join('.', os.path.basename(script))]))

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
