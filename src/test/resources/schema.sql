create table if not exists category
(
    id serial
        constraint category_pk
            primary key,
    name varchar default 'UnTittled'::character varying not null
        constraint category_pk_3
            unique,
    parent_id integer default 0 not null,
    create_time bigint default 0 not null,
    update_time bigint default 0 not null
);

comment on table category is '分类';

alter table category owner to postgres;

create table if not exists category_post
(
    id serial
        constraint category_post_pk
            primary key,
    post_id integer not null,
    category_id integer default 0 not null,
    create_time bigint default 0 not null,
    update_time bigint default 0 not null,
    constraint category_post_pk_2
        unique (post_id, category_id)
);

alter table category_post owner to postgres;

create table if not exists setting
(
    k varchar not null
        constraint setting_pk
            primary key,
    v varchar default ''::character varying not null,
    option_type boolean default false not null,
    create_time bigint default 0 not null,
    update_time bigint default 0 not null
);

alter table setting owner to postgres;

create table if not exists post
(
    id serial
        constraint post_pk
            primary key,
    author_id integer default 0 not null,
    title varchar default ''::character varying not null,
    content text default ''::text not null,
    status boolean default false not null,
    cover varchar default ''::character varying not null,
    post_type boolean default true not null,
    create_time bigint default 0 not null,
    update_time bigint default 0 not null,
    summary varchar default ''::character varying not null
);

alter table post owner to postgres;

create table if not exists tag
(
    id serial
        constraint tag_pk
            primary key,
    name varchar default ''::character varying not null
        constraint tag_pk_3
            unique,
    create_time bigint default 0 not null,
    update_time bigint default 0 not null
);

alter table tag owner to postgres;

create table if not exists tag_post
(
    id serial
        constraint tag_post_pk
            primary key,
    post_id integer default 0 not null,
    tag_id integer default 0 not null,
    create_time bigint default 0 not null,
    update_time bigint default 0 not null
);

alter table tag_post owner to postgres;

create table if not exists "user"
(
    id serial
        constraint user_pk
            primary key,
    username varchar default ''::character varying not null
        constraint user_pk_2
            unique,
    password varchar default ''::character varying not null,
    nickname varchar default 'User'::character varying not null,
    avatar varchar default ''::character varying not null,
    email varchar default ''::character varying not null,
    phone varchar default ''::character varying not null,
    role varchar default 'USER'::character varying not null,
    jwt varchar default ''::character varying not null,
    totp varchar default ''::character varying not null,
    salt varchar default ''::character varying not null,
    create_time bigint default 0 not null,
    update_time bigint default 0 not null,
    last_login_time bigint default 0 not null
);

alter table "user" owner to postgres;

