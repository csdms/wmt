#! /usr/bin/env python
from __future__ import absolute_import
import os
from string import Template

from cmtweb.site import Site


def main():
    import argparse
    parser = argparse.ArgumentParser('Setup CMTweb at a site.')
    parser.add_argument('prefix', help='path to the cmtweb project')

    args = parser.parse_args()

    site = Site(args.prefix)
    site.create()

    snippet = Template("""
    WSGIScriptAlias /cmtweb/ ${prefix}/bin/cmtweb_wsgi_main.py/

    Alias /cmtweb/static ${prefix}/static
    AddType text/html .py
    <Directory ${prefix}/>
      Order deny,allow
      148   Allow from all
    </Directory>
    """)

    print(snippet.substitute(prefix=site.prefix))


if __name__ == '__main__':
    main()
