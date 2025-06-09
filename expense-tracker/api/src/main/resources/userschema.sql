create table User(
  userid bigint not  null,
  user_name varchar(255) not null,
  email varchar(255) not null,
  password varchar(255) not null,
  primary key (userid)
);