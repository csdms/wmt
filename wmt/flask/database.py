from sqlalchemy import create_engine
from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base


SESSIONS = {}
for db in ['names', 'tag', 'users', 'wmt', 'submission']:
    engine = create_engine(
        'sqlite:////data/web/htdocs/wmt/api/v1/db/%s.db' % db,
        convert_unicode=True)
    db_session = scoped_session(sessionmaker(autocommit=False,
                                             autoflush=False,
                                             bind=engine))
    Base = declarative_base()
    Base.query = db_session.query_property()

    SESSIONS[db] = dict(session=db_session, base=Base)


def init_db():
    # import all modules here that might define models so that
    # they will be registered properly on the metadata.  Otherwise
    # you will have to import them first before calling init_db()
    from . import tags

    Base.metadata.create_all(bind=engine)

