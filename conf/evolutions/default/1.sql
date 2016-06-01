# --- !Ups

create table "suppliers" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"desc" VARCHAR(254) NOT NULL);

create table "Producto" ("id" BIGSERIAL NOT NULL PRIMARY KEY, "producto" VARCHAR(254) NOT NULL, "precio_actual" INTEGER, "calorias" INTEGER )
# --- !Downs
;
drop table "suppliers";

drop table "Producto"