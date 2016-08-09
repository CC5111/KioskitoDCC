# --- !Ups

create table "Product" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "product" VARCHAR(254) NOT NULL,
    "calories" INTEGER,
    "current_price" INTEGER,
    "current_stock" INTEGER);

create table "Purchase" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "date" TIMESTAMP NOT NULL);

create table "Count" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "date" TIMESTAMP,
    "actual_earnings" INTEGER
);

create table "CountDetailByProduct" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "count_id" INT NOT NULL,
    "product_id" INT NOT NULL,
    "quantity" INTEGER,
    "sold_quantity" INTEGER,
    "selling_price" INTEGER);

create table "PurchaseDetailByProduct" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "product_id" INT NOT NULL,
    "purchase_id" INT NOT NULL ,
    "number_of_packages" INTEGER,
    "quantity_by_package" INTEGER,
    "price_per_package" INTEGER);

# --- !Downs
;
drop table "suppliers";

drop table "Product";

drop table "Period";

drop table "Count"
