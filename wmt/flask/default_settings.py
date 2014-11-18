import os


_BASE_DIR = os.path.abspath(os.path.dirname(__file__))

DEBUG = True
SECRET_KEY = 'super-secret-key'
SERVER_NAME = 'csdms.colorado.edu'
UPLOADS_DEFAULT_DEST = os.path.join(_BASE_DIR, 'files/uploads')
UPLOAD_DIR = os.path.join(_BASE_DIR, 'files/uploads')
STAGE_DIR = os.path.join(_BASE_DIR, 'files/downloads')
DATABASE_DIR = os.path.join(_BASE_DIR, 'db')

CRYPT_INI = """
[passlib]
schemes = sha512_crypt, sha256_crypt
sha256_crypt__default_rounds = 100000
sha512_crypt__default_rounds = 100000
""".strip()
