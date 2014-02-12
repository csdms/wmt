from __future__ import print_function

import string
import sys
import shutil
import types


class TemplateFile(object):
    def __init__(self, filename):
        self._template = TemplateFile.read_template(filename)

    @classmethod
    def read_template(filename):
        with open(filename, 'r') as src:
            return string.Template(src.read())

    def substitute(self, mapping):
        return self._template.substitute(mapping)

    def safe_substitute(self, mapping):
        return self._template.safe_substitute(mapping)

    def write(self, mapping, file=sys.stdout):
        if isinstance(file, types.StringTypes):
            with open(file, 'w') as dest:
                dest.write(self.substitute(mapping))
        else:
            file.write(self.substitute(mapping))


class TemplateFiles(object):
    def __init__(self, filenames):
        self._templates = TemplateFiles.read_templates(filenames)

    @classmethod
    def read_templates(filenames):
        if isinstance(filenames, types.StringTypes):
            filenames = [filenames]

        templates = dict()
        for filename in filenames:
            self.templates[filename] = TemplateFile(filename)

        return templates

    def substitute(self, mapping):
        subs = {}
        for (filename, template) in self._templates.items():
            subs[filename] = template.substitute(mapping)
        return subs

    def write(self, mapping, files={}):
        for (filename, template) in self._templates.items():
            if files.has_key(filename):
                template.write(mapping, file=filename)
            else:
                template.write(mapping)
