create table public.category
(
    id          serial
        constraint category_pk
            primary key,
    name        varchar default 'UnTittled'::character varying not null
        constraint category_pk_3
            unique,
    parent_id   integer default 0                              not null,
    create_time bigint,
    update_time bigint
);

comment on table public.category is '分类';

alter table public.category
    owner to postgres;

create table public.category_post
(
    id          serial
        constraint category_post_pk
            primary key,
    post_id     integer not null,
    category_id integer,
    create_time bigint,
    update_time bigint,
    constraint category_post_pk_2
        unique (post_id, category_id)
);

alter table public.category_post
    owner to postgres;

create table public.setting
(
    k           varchar               not null
        constraint setting_pk
            primary key,
    v           varchar,
    option_type boolean default false not null,
    create_time bigint,
    update_time bigint
);

alter table public.setting
    owner to postgres;

create table public.post
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
    update_time bigint,
    summary     varchar
);

alter table public.post
    owner to postgres;

create table public.tag
(
    id          serial
        constraint tag_pk
            primary key,
    name        varchar
        constraint tag_pk_3
            unique,
    create_time bigint,
    update_time bigint
);

alter table public.tag
    owner to postgres;

create table public.tag_post
(
    id          serial
        constraint tag_post_pk
            primary key,
    post_id     integer,
    tag_id      integer,
    create_time bigint,
    update_time bigint
);

alter table public.tag_post
    owner to postgres;

create table public."user"
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

alter table public."user"
    owner to postgres;

