create table category
(
    id          serial
        constraint category_pk
            primary key,
    name        varchar default 'UnTittled'::character varying not null,
    uniq_name   varchar default 'default'::character varying   not null
        constraint category_pk_2
            unique,
    parent_id   integer default 0                              not null,
    create_time bigint,
    update_time bigint
);

comment on table category is '分类';

alter table category
    owner to postgres;

create table category_post
(
    id          serial
        constraint category_post_pk
            primary key,
    post_id     integer not null,
    category_id integer,
    create_time bigint,
    update_time bigint
);

alter table category_post
    owner to postgres;

create table setting
(
    id          serial
        constraint setting_pk_2
            primary key,
    k           varchar               not null
        constraint setting_pk
            unique,
    v           varchar,
    option_type boolean default false not null,
    create_time bigint,
    update_time bigint
);

alter table setting
    owner to postgres;

create table post
(
    id          serial
        constraint post_pk
            primary key,
    author_id   integer default 0 not null,
    title       varchar,
    content     text,
    status      boolean,
    cover       varchar,
    post_type   boolean,
    create_time bigint,
    update_time bigint
);

alter table post
    owner to postgres;

create table tag
(
    id          serial
        constraint tag_pk
            primary key,
    name        varchar,
    uniq_name   varchar
        constraint tag_pk_2
            unique,
    create_time bigint,
    update_time bigint
);

alter table tag
    owner to postgres;

create table tag_post
(
    id          serial
        constraint tag_post_pk
            primary key,
    post_id     integer,
    tag_id      integer,
    create_time bigint,
    update_time bigint
);

alter table tag_post
    owner to postgres;

create table "user"
(
    id              serial
        constraint user_pk
            primary key,
    username        varchar not null
        constraint user_pk_2
            unique,
    password        varchar,
    nickname        varchar not null,
    avatar          varchar,
    email           varchar,
    phone           varchar,
    role            varchar default 'USER'::character varying,
    jwt             varchar,
    totp            varchar,
    salt            varchar not null,
    create_time     bigint,
    update_time     bigint,
    last_login_time bigint
);

alter table "user"
    owner to postgres;

