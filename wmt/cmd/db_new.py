from __future__ import print_function
import sys
import os

from ..installer.site import create_empty_database
from ..schema import SCHEMA


def main():
    import argparse


    parser = argparse.ArgumentParser()

    parser.add_argument('SCHEMA', nargs='*', default=None,
                        help='name of wmt database')
    parser.add_argument('--clobber', action='store_true',
                        help='clobber existing database file')

    args = parser.parse_args()

    schemas = args.SCHEMA or SCHEMA.keys()

    for schema in schemas:
        try:
            script = SCHEMA[schema]
        except KeyError:
            print('%s: unknown wmt database' % schema, file=sys.stderr)
        else:
            print(schema + '.db', file=sys.stderr)
            create_empty_database(schema + '.db', clobber=args.clobber,
                                  schema=script)
