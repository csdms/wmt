create table session (
  session_id CHAR(128) UNIQUE NOT NULL,
  atime DATETIME NOT NULL default current_timestamp,
  data TEXT
);
