#!/usr/bin/env python

from ez_setup import use_setuptools

use_setuptools()

from setuptools import setup
from setuptools.command.install import install

from wmt import __version__


setup(name='wmt',
      version=__version__,
      description='Web app for the Component Modeling Tool',
      author='Eric Hutton',
      author_email='eric.hutton@colorado.edu',
      url=' http://csdms.colorado.edu/',
      install_requires=['web.py', 'PyYAML>=3.10', 'passlib', ],
      packages=['wmt', 'wmt.scripts', 'wmt.models', ],
      long_description="Create, save, edit, run collections of connected components.",
      license="Public domain",
      platforms=["any"],
      package_data={
          '': ['data/*json',
               'templates/*html', ], },
      entry_points={
          'console_scripts': [
              'wmt_setup = wmt.scripts.site_setup:main',
          ],
      },
     )
