create table tags (
  id       integer primary key,
  tag      text,
  owner    text
);
create table model_tags (
  model_id       integer,
  tag_id         integer
);
