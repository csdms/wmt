
_SESSION = """
create table session (
  session_id CHAR(128) UNIQUE NOT NULL,
  atime DATETIME NOT NULL default current_timestamp,
  data TEXT
);""".strip()

_SUBMISSION = """
create table submission (
  id            integer primary key,
  model_id      integer,
  uuid          text,
  name          text,
  status        text,
  created       text,
  updated       text,
  message       text,
  owner         text,
  stage_dir     text
);
create table history (
  id            integer primary key,
  submission_id integer,
  updated       text,
  message       text
);""".strip()

_TAGS = """
create table tags (
  id       integer primary key,
  tag      text,
  owner    text
);
create table model_tags (
  model_id       integer,
  tag_id         integer
);""".strip()

_USERS = """
create table users (
  id            integer primary key,
  username      text,
  password      text,
  unique (username)
);""".strip()

_WMT = """
create table models (
  id            integer primary key,
  name          text,
  date          text,
  json          text,
  owner         text
);
create table components (
  id            integer primary key,
  name          text,
  json          text
);
create table hosts (
  id            integer primary key,
  username      text,
  password      text,
  host          text,
  command       text,
  unique (username)
);""".strip()

SCHEMA = {
    'session': _SESSION,
    'submission': _SUBMISSION,
    'tag': _TAGS,
    'users': _USERS,
    'wmt': _WMT,
}
