from __future__ import print_function

import os
import re
import string
import sys
import shutil
import types

ID_PATTERN_STRING = '\$\{(?P<id>[_a-zA-Z][_a-zA-Z]*)\}'
ID_PATTERN = re.compile(ID_PATTERN_STRING)
_INTEGER_PRESENTATION_TYPES = set(['b', 'c', 'd', 'o', 'x', 'X', 'n', None])
_FLOAT_PRESENTATION_TYPES = set(['f', 'e', 'E', 'g', 'G'])


class FileFormatter(string.Formatter):
    def __init__(self, defaults):
        string.Formatter.__init__(self)
        self._defaults = defaults
        self._missing = set()
        self._used = set()

    @property
    def missing_fields(self):
        return self._missing

    def clear_missing(self):
        self._missing = set()

    def _get_value_from_keywords(self, key, kwds):
        try:
            return kwds[key]
        except KeyError:
            return self._defaults[key]

    def get_value(self, key, args, kwds):
        if isinstance(key, types.StringTypes):
            try:
                value = self._get_value_from_keywords(key, kwds)
            except KeyError:
                value = '{%s}' % key
                self._missing.add(key)
            else:
                self._used.add(key)
            finally:
                return value
        else:
            string.Formatter.get_value(self, key, args, kwds)


    def format_field(self, value, format_spec):
        try:
            return string.Formatter.format_field(self, value, format_spec)
        except ValueError as error:
            presentation_type = format_spec[-1]
            if presentation_type in _INTEGER_PRESENTATION_TYPES:
                return string.Formatter.format_field(self, int(value),
                                                     format_spec)
            elif presentation_type in _FLOAT_PRESENTATION_TYPES:
                return string.Formatter.format_field(self, float(value),
                                                     format_spec)
            else:
                raise error

    def lint(self):
        warnings = []
        for field in self._missing:
            warnings.append('missing: %s' % field)
        return warnings


class TemplateFile(object):
    def __init__(self, filename):
        self._template = TemplateFile.read_template(filename)
        self._filename = filename
        self._identifiers = set(ID_PATTERN.findall(self.contents))

    @property
    def contents(self):
        return self._template.template

    @staticmethod
    def read_template(filename):
        with open(filename, 'r') as src:
            return string.Template(src.read())

    def missing(self, mapping):
        return self._identifiers - set(mapping)

    def extra(self, mapping):
        return set(mapping) - self._identifiers

    def substitute(self, mapping):
        return self._template.substitute(mapping)

    def safe_substitute(self, mapping):
        return self._template.safe_substitute(mapping)

    def write(self, mapping, file=None, clobber=False):
        if file is None:
            file = self._filename

        if isinstance(file, types.StringTypes):
            (base, ext) = os.path.splitext(file)
            if ext == '.tmpl':
                file = base

            if os.path.isfile(file) and not clobber:
                raise ValueError(file)

            with open(file, 'w') as dest:
                dest.write(self.safe_substitute(mapping))
        else:
            file.write(self.safe_substitute(mapping))

    def validate(self, mapping, with_reasons=False):
        missing_keys = self.missing(mapping)

        if with_reasons:
            reasons = []
            for missing_key in missing_keys:
                reasons.append('missing: %s' % missing_key)
            return (len(missing_keys) == 0, reasons)
        else:
            return len(missing_keys) == 0


