drop table if exists users cascade;
drop table if exists items cascade;
drop table if exists bookings;
drop index if exists bookings_start_date_index;
drop index if exists comments_created_index;
drop table if exists requests;
drop table if exists comments;


create table if not exists users
(
    id    serial primary key,
    name  varchar(255) not null,
    email varchar(512) not null,
    constraint uq_user_email
        unique (email)

);

create table if not exists requests
(
    id           serial primary key,
    description  varchar(512) not null,
    requestor_id bigint       not null,
    created      timestamp    not null,
    constraint requests_users_id_fk
        foreign key (requestor_id) references users
);

create table if not exists items
(
    id           serial primary key,
    name         varchar(255) not null,
    description  varchar(512) not null,
    is_available boolean      not null,
    owner_id     bigint       not null,
    request_id   bigint,
    constraint items_users_id_fk
        foreign key (owner_id) references users on delete cascade,
    constraint items_requests_id_fk
        foreign key (request_id) references requests
            on delete cascade
);

create table if not exists bookings
(
    id         serial primary key,
    start_date timestamp not null,
    end_date   timestamp not null,
    item_id    bigint    not null,
    booker_id  bigint    not null,
    status     varchar   not null,
    constraint bookings_items_id_fk
        foreign key (item_id) references items on delete cascade,
    constraint bookings_users_id_fk
        foreign key (booker_id) references users on delete cascade
);

create index if not exists bookings_start_date_index
    on bookings (start_date);

create table if not exists comments
(
    id        serial primary key,
    text      varchar(512) not null,
    item_id   bigint       not null,
    author_id bigint       not null,
    created   timestamp    not null,
    constraint comments_items_id_fk
        foreign key (item_id) references items on delete cascade,
    constraint comments_users_id_fk
        foreign key (author_id) references users on delete cascade
);

create index if not exists comments_created_index
    on comments (created);

