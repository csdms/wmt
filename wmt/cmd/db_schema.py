from __future__ import print_function
import sys

from ..schema import SCHEMA


def main():
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('SCHEMA', help='name of wmt database')

    args = parser.parse_args()
    try:
        print(SCHEMA[args.SCHEMA])
    except KeyError:
        print('%s: unknown wmt database' % args.SCHEMA, file=sys.stderr)
