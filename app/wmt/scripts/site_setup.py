#! /usr/bin/env python
from __future__ import absolute_import
import os
from string import Template

from wmt.site import Site


def setup(args):
    site = Site(args.prefix)
    site.create()


def main():
    import argparse
    parser = argparse.ArgumentParser('Setup WMT at a site.')
    parser.add_argument('prefix', help='path to the WMT project')

    args = parser.parse_args()

    setup(args)

    epilog = Template(
"""
A CMT project has been created for you under:

    ${prefix}

To finish the installation you'll have to do the following:

1. If you're using Apache, add the following lines to httpd.conf:

    WSGIScriptAlias /wmt/ ${prefix}/bin/wmt_wsgi_main.py/

    Alias /wmt/static ${prefix}/static
    AddType text/html .py
    <Directory ${prefix}/>
      Order deny,allow
      148   Allow from all
    </Directory>

2. Be sure the permissions and ownership are correct for the database files.

    > chown -R nobody:nobody ${prefix}/db

3. Restart Apache:

    > apachectl -k restart
""")

    print(epilog.substitute(prefix=site.prefix))


if __name__ == '__main__':
    main()
