#!/usr/bin/env python

from ez_setup import use_setuptools

use_setuptools()

from setuptools import setup, find_packages
from setuptools.command.install import install

from wmt import __version__


setup(name='wmt',
      version=__version__,
      description='Web app for the Component Modeling Tool',
      author='Eric Hutton',
      author_email='eric.hutton@colorado.edu',
      url=' http://csdms.colorado.edu/',
      install_requires=['web.py', 'PyYAML>=3.10', 'passlib', ],
      packages=find_packages(),
      #packages=['wmt', 'wmt.scripts', 'wmt.models', ],
      long_description="Create, save, edit, run collections of connected components.",
      license="Public domain",
      platforms=["any"],
      package_data={
          '': [#'data/db/components/*',
               'data/statis/*css',
               'data/templates/*html', ], },
      entry_points={
          'console_scripts': [
              'wmt_setup = wmt.installer.site_setup:main',
              'wmt_fill_file = wmt.scripts.fill_file:main',
              'wmt_validate = wmt.scripts.validate:main',
              'wmt_get_db = wmt.installer.components:main',
          ],
      },
     )
