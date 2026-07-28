CREATE DATABASE IF NOT EXISTS orders
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE orders;

create table orders (
  id varchar(36) primary key,
  customer_id varchar(36),
  status varchar(255),
  total_amount int
);

create table order_items (
  id varchar(36) primary key,
  order_id varchar(36),
  product_id varchar(36),
  price int,
  quantity int,
  constraint fk_order_items_order_id foreign key (order_id) references orders(id)
);
