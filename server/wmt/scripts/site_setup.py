#! /usr/bin/env python
from __future__ import absolute_import
import os
from string import Template
from collections import OrderedDict

from wmt.site import Site


def setup(prefix, options={}):
    site = Site(prefix, options=options)
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


def main():
    import argparse

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

    args = parser.parse_args()

    user_vars = read_site_vars(args.file)
    user_vars.update(collect_user_vars(args.conf))
    user_vars.update([('name', args.name),
                      ('email', args.email),
                      ('url_scheme', args.url_scheme),
                      ('url_netloc', args.url_netloc),
                      ('url_path', args.url_path), ])

    setup(args.prefix, user_vars)

    epilog = Template(
"""
A CMT project has been created for you under:

    ${prefix}

To finish the installation you'll have to do the following:

1. If you're using Apache, add the following lines to httpd.conf:

    WSGIScriptAlias /wmt/ ${prefix}/bin/start_wmt.wsgi/

    Alias /wmt/static ${prefix}/static
    <Directory ${prefix}/>
      Order deny,allow
      148   Allow from all
    </Directory>

   This will run your application in *embedded* mode. To run in *daemon* mode,
   add the following additional lines,

    WSGIDaemonProcess ${netloc} threads=15 maximum-requests=10000
    WSGIProcessGroup ${netloc}

2. Be sure the permissions and ownership are correct for the database files.

    > chown -R nobody:nobody ${prefix}/db

3. Restart Apache:

    > apachectl -k restart
""")

    print(epilog.substitute(prefix=args.prefix, netloc=args.url_netloc))


if __name__ == '__main__':
    main()
