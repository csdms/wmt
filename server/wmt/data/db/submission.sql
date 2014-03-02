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
