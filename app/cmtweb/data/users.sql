create table users (
  id            integer primary key,
  username      text,
  password      text,
  unique (username)
);
