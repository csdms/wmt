from os import path


DEBUG = True
SECRET_KEY = 'super secret key'
CRYPT_INI_CONTENTS = """
[passlib]
schemes = sha512_crypt, sha256_crypt
sha256_crypt__default_rounds = 100000
sha512_crypt__default_rounds = 100000
""".strip()
DATABASE_DIR = '/Users/huttone/git/wmt/db'

SQLALCHEMY_MIGRATE_REPO = path.join(DATABASE_DIR, 'db_repository')
SQLALCHEMY_DATABASE_URI = 'sqlite:///' + path.join(DATABASE_DIR, 'wmt.db')
SQLALCHEMY_BINDS = {
    'names': 'sqlite:///' + path.join(DATABASE_DIR, 'names.db'),
    'tags': 'sqlite:///' + path.join(DATABASE_DIR, 'tag.db'),
    'users': 'sqlite:///' + path.join(DATABASE_DIR, 'users.db'),
    'sims': 'sqlite:///' + path.join(DATABASE_DIR, 'submission.db'),
    'models': 'sqlite:///' + path.join(DATABASE_DIR, 'models.db'),
}
