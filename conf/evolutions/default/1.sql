# --- !Ups

create table "suppliers" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"desc" VARCHAR(254) NOT NULL);

create table "Product" ("id" BIGSERIAL NOT NULL PRIMARY KEY, "product" VARCHAR(254) NOT NULL, "calories" INTEGER );

# --- !Downs
;
drop table "suppliers";

drop table "Product";
