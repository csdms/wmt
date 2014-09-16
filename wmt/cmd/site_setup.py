#! /usr/bin/env python
import os
from string import Template
from collections import OrderedDict

from ..installer.site import Site


_HTTPD_CONF = """
WSGIScriptAlias /wmt/ {startup_script}/

Alias /wmt/static {static_dir}
<Directory {prefix}>
    Order deny,allow
    Allow from all
</Directory>

This will run your application in *embedded* mode. To run in *daemon* mode,
add the following additional lines,

WSGIDaemonProcess {netloc} threads=15 maximum-requests=10000 python-path={python_path}
WSGIProcessGroup {netloc}
"""


_POST_SETUP_INSTRUCTIONS = """
A CMT project has been created for you under:

    {prefix}

To finish the installation you'll have to do the following:

1. If you're using Apache, add the following lines to httpd.conf:

{httpd_conf}

2. Be sure the permissions and ownership are correct for the database files.

    > chown -R nobody:nobody {db_dir}

3. Restart Apache:

    > apachectl -k restart
"""

def setup(prefix, options={}):
    site = Site(os.path.abspath(prefix), options=options)
    site.create()


def collect_user_vars(arg_values):
    values = []
    for value in arg_values:
        values.extend(value.split(','))
    return [value.split('=') for value in values]


def read_site_vars(filename):
    from ConfigParser import ConfigParser, NoSectionError

    parser = ConfigParser()
    parser.read([filename])

    try:
        return dict(parser.items('wmt'))
    except NoSectionError:
        return dict()


def print_post_setup_instructions(args):
    def indent_multiline(s, indent=4):
        import re
        return re.sub('^', " " * indent, s, flags=re.MULTILINE)

    prefix = os.path.abspath(args.prefix)
    site_vars = {
        'prefix': prefix,
        'netloc': args.url_netloc,
        'db_dir': os.path.join(prefix, 'db'),
        'static_dir': os.path.join(prefix, 'static'),
        'startup_script': os.path.join(prefix, 'bin', 'start_wmt.wsgi'),
        'python_path': os.path.join(prefix, 'internal'),
    }
    httpd_conf = _HTTPD_CONF.format(**site_vars)

    if args.httpd_conf:
        print(httpd_conf)
    else:
        site_vars['httpd_conf'] = indent_multiline(httpd_conf, indent=4)
        print(_POST_SETUP_INSTRUCTIONS.format(**site_vars))


def parse_args():
    import argparse


    class SetAsDryRun(argparse.Action):
        def __call__(self, parser, namespace, values, option_string=None):
            setattr(namespace, self.dest, True)
            setattr(namespace, 'dry_run', True)

    parser = argparse.ArgumentParser('Setup wmt at a site.')
    parser.add_argument('prefix', help='path to the wmt project')
    parser.add_argument('--name', default='<your-name>',
                        help='name of contact')
    parser.add_argument('--email', default='<your-email>',
                        help='name of contact')
    parser.add_argument('--conf', action='append', default=[],
                        help='give a custom configuration value')
    parser.add_argument('--file', default='site.cfg',
                        help='read options from a files')

    parser.add_argument('--url-scheme', default='https',
                        help='URL scheme specifier')
    parser.add_argument('--url-path', default='wmt-server',
                        help='Hierarchical path to the WMT server')
    parser.add_argument('--url-netloc', default='localhost',
                        help='Network location of the WMT server')

    parser.add_argument('--pickup-scheme', default='https',
                        help='URL scheme specifier for the pickup')
    parser.add_argument('--pickup-path', default='pickup',
                        help='Hierarchical path to the pickup')
    parser.add_argument('--pickup-netloc', default='localhost',
                        help='Network location of the pickup')

    parser.add_argument('--dry-run', action='store_true',
                        help='go through the motions')
    parser.add_argument('--httpd-conf', action=SetAsDryRun, nargs=0,
                        default=False,
                        help='pring httpd.conf configuration and exit')

    return parser.parse_args()


def main():
    args = parse_args()

    user_vars = dict([
        ('name', args.name),
        ('email', args.email),
        ('url_scheme', args.url_scheme),
        ('url_netloc', args.url_netloc),
        ('url_path', args.url_path),
        ('pickup_scheme', args.pickup_scheme),
        ('pickup_netloc', args.pickup_netloc),
        ('pickup_path', args.pickup_path),
    ])
    user_vars.update(collect_user_vars(args.conf))
    user_vars.update(read_site_vars(args.file))

    if not args.dry_run:
        setup(args.prefix, user_vars)

    print_post_setup_instructions(args)


if __name__ == '__main__':
    main()
