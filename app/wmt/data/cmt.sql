create table models (
  id            integer primary key,
  name          text,
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
);
