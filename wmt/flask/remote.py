import paramiko
import getpass


class Error(Exception):
    pass


class AuthenticationError(Error):
    def __init__(self, host, username):
        self.host = host
        self.username = username

    def __str__(self):
        return 'unable to authenticate %s@%s' % (self.host, self.username)


class ConnectionError(Error):
    def __init__(self, host, message):
        self.host = host
        self.message = message

    def __str__(self):
        return '%s: unable to connect: %s' % (self.host, self.message)


class RemoteError(Error):
    def __init__(self, errno, message):
        self.errno = errno
        self.message = message

    def __str__(self):
        return 'unknown error %d: %s' % (self.errno, self.message)


def open_connection_to_host(host, username=None, password=None, prompt=True):
    import socket

    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    if password is None and prompt:
        password = getpass.getpass()

    try:
        ssh.connect(host, username=username, password=password)
    except paramiko.AuthenticationException:
        raise AuthenticationError(host, username)
    except socket.error as error:
        raise ConnectionError(host, str(error))

    return ssh


def exec_on_ssh_client(ssh, cmd):
    chan = ssh.get_transport().open_session()
    chan.exec_command(cmd)

    status = chan.recv_exit_status()
    if status == 0:
        return chan.recv(-1)
    else:
        raise RemoteError(status, chan.recv_stderr(-1))


def remote(host, cmd, username=None, password=None, prompt=True):
    ssh = open_connection_to_host(host, username=username, password=password,
                                  prompt=prompt)
    return exec_on_ssh_client(ssh, cmd)


def main():
    host, cmd = ('river.colorado.edu', '/usr/local/maui/bin/showq')

    print 'Running command on remote: %s' % host
    try:
        print remote(host, cmd)
    except Error as error:
        print str(error)


if __name__ == '__main__':
    main()
