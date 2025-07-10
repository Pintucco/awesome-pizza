create sequence  seq_awesome_pizza_user start with 1 increment by 1;

create table  awesome_pizza_user (
id bigint not null,
username varchar(255) NOT NULL,
password varchar(255) NOT NULL,
primary key (id)
);

create sequence  seq_pizza_recipe start with 1 increment by 1;

create table  pizza_recipe (
id bigint not null,
name varchar(255) NOT NULL,
description varchar(1000) NOT NULL,
default_price double precision NOT NULL,
active boolean not null,
primary key (id)
);

create sequence  seq_pizza_order start with 1 increment by 1;
create table  pizza_order (
id bigint not null,
code varchar(255) NOT NULL UNIQUE,
order_status varchar(255) NOT NULL,
price double precision NOT NULL,
submitted_at timestamp with time zone null,
accepted_refused_at  timestamp with time zone null,
concluded_at timestamp with time zone null,
worked_by bigint null references awesome_pizza_user(id),
primary key (id)
);



create sequence  seq_pizza_order_item start with 1 increment by 1;
create table  pizza_order_item (
id bigint not null,
order_id bigint not null references pizza_order(id),
dough_type varchar(255) NOT NULL,
price double precision NOT NULL,
pizza_recipe bigint not null references pizza_recipe(id),
primary key (id)
);




