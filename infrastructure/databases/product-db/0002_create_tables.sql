drop table if exists products;
drop table if exists categories;

create table categories (
  id varchar(36) primary key,
  name varchar(255),
  parent_id varchar(255),
  is_deleted tinyint(1),
  created_date datetime,
  created_by varchar(255),
  last_modified_date datetime,
  last_modified_by varchar(255)
);

create table products (
  id varchar(36) primary key,
  name varchar(255) not null,
  price int,
  stock int,
  category_id varchar(36),
  is_deleted tinyint(1),
  created_date datetime,
  created_by varchar(255),
  last_modified_date datetime,
  last_modified_by varchar(255),
  constraint fk_products_category_id foreign key (category_id) references categories(id)
);
