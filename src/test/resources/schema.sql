create table category (
                          id int primary key not null auto_increment,
                          name varchar not null default 'UnTittled',
                          uniq_name varchar not null default 'default',
                          parent_id int not null default 0,
                          create_time bigint,
                          update_time bigint
);

create unique index category_pk_2 on category (uniq_name);
comment on table category is '分类';

create table category_post (
                               id int primary key not null auto_increment,
                               post_id int not null,
                               category_id int,
                               create_time bigint,
                               update_time bigint
);

create table setting (
                         id int primary key not null auto_increment,
                         k varchar not null,
                         v varchar,
                         option_type boolean not null default false,
                         create_time bigint,
                         update_time bigint
);

create unique index option_pk_2 on setting (k);

create table post (
                      id int primary key not null auto_increment,
                      author_id int not null default 0,
                      title varchar,
                      content text,
                      status boolean,
                      cover varchar,
                      post_type boolean,
                      create_time bigint,
                      update_time bigint
);

create table tag (
                     id int primary key not null auto_increment,
                     name varchar,
                     uniq_name varchar,
                     create_time bigint,
                     update_time bigint
);

create unique index tag_pk_2 on tag (uniq_name);

create table tag_post (
                          id int primary key not null auto_increment,
                          post_id int,
                          tag_id int,
                          create_time bigint,
                          update_time bigint
);

create table "user" (
                        id int primary key not null auto_increment,
                        username varchar not null,
                        password varchar,
                        nickname varchar not null,
                        avatar varchar,
                        email varchar,
                        phone varchar,
                        role varchar default 'USER',
                        jwt varchar,
                        totp varchar,
                        salt varchar not null,
                        create_time bigint,
                        update_time bigint,
                        last_login_time bigint
);

create unique index user_pk_2 on "user" (username);
