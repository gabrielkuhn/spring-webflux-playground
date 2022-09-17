create table if not exists customer
(
    id serial
        constraint customer_pk
            primary key,
    name varchar not null
);