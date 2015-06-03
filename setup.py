#!/usr/bin/env python

from ez_setup import use_setuptools

use_setuptools()

from setuptools import setup, find_packages
from setuptools.command.install import install

from wmt import __version__


setup(name='wmt',
      version=__version__,
      description='RESTful API for the Web Modeling Tool',
      author='Eric Hutton',
      author_email='eric.hutton@colorado.edu',
      url=' http://csdms.colorado.edu/',
      install_requires=['flask', 'flask-sqlalchemy', 'flask-testing',
                        'flask-login', 'sqlalchemy-migrate', 'PyYAML>=3.10',
                        'passlib', 'sphinxcontrib-httpdomain==1.1.8',
                        'cmtstandardnames', 'paramiko']
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
              'wmt-site-setup = wmt.cmd.site_setup:main',
              'wmt-site-conf = wmt.cmd.site_conf:main',
              'wmt-db-new = wmt.cmd.db_new:main',
              'wmt-db-schema = wmt.cmd.db_schema:main',
              'wmt-db-update = wmt.cmd.db_update:main',
              'wmt_fill_file = wmt.scripts.fill_file:main',
              'wmt_validate = wmt.scripts.validate:main',
              'wmt_get_db = wmt.installer.components:main',
          ],
      },
     )
