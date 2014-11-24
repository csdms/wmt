import os


class WmtSettings(object):
    DEBUG = True
    SECRET_KEY = 'secret-key'
    CRYPT_INI_CONTENTS = """
[passlib]
schemes = sha512_crypt, sha256_crypt
sha256_crypt__default_rounds = 100000
sha512_crypt__default_rounds = 100000
""".strip()

    def __init__(self, dir=None):
        self._root_dir = dir or os.getcwd()
        self._db_dir = os.path.join(self._root_dir, 'db')
        if not os.path.exists(self._db_dir):
            os.mkdir(self._db_dir)

    @property
    def WMT_ROOT_DIR(self):
        return self._root_dir

    @property
    def STAGE_DIR(self):
        return os.path.join(self._root_dir, 'files', 'download')

    @property
    def WMT_DATABASE_DIR(self):
        return self._db_dir

    @property
    def SQLALCHEMY_DATABASE_URI(self):
        return self.sqlite_db_path('wmt.db')

    @property
    def SQLALCHEMY_BINDS(self):
        return {'names': self.sqlite_db_path('names.db'),
                'tags':  self.sqlite_db_path('tag.db'),
                'users':  self.sqlite_db_path('users.db'),
                'sims':  self.sqlite_db_path('submission.db'),
                'models':  self.sqlite_db_path('models.db'), }

    def sqlite_db_path(self, filename):
        if self._db_dir is not None:
            return 'sqlite:///' + os.path.join(self._db_dir, filename)
        else:
            return 'sqlite:///:memory:'


class WmtDebugSettings(WmtSettings):
    DEBUG = True
    SECRET_KEY = 'super-secret-key'
