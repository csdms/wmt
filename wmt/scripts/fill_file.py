#! /usr/bin/env python
from __future__ import print_function


def substitute(mapping, filenames, warnings=False, clobber=False):
    import sys

    from ..utils.templatefile import TemplateFile
    from ..utils.json import load_mapping

    for filename in filenames:
        template = TemplateFile(filename)
        template.write(mapping, clobber=clobber)

        if warnings:
            for missing_key in template.missing(mapping):
                print('missing: %s' % missing_key, file=sys.stderr)


def main():
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('dict', type=str,
                        help='dictionary file')
    parser.add_argument('files', metavar='template', nargs='+',
                        help='template files to fill')
    parser.add_argument('--warn', action='store_true', default=False,
                        help='be verbose with warnings')
    parser.add_argument('--clobber', action='store_true', default=False,
                        help='clobber output file, if it exists')

    args = parser.parse_args()

    mapping = load_mapping(args.dict)

    substitute(mapping, args.files, warnings=args.warn,
               clobber=args.clobber)


if __name__ == '__main__':
    main()
