# --- !Ups

create table "suppliers" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"desc" VARCHAR(254) NOT NULL);

create table "Product" ("id" BIGSERIAL NOT NULL PRIMARY KEY, "product" VARCHAR(254) NOT NULL, "calories" INTEGER );

create table "Period" ("id" BIGSERIAL NOT NULL PRIMARY KEY, "starting_date" TIMESTAMP NOT NULL, "end_date" TIMESTAMP , "earnings" INTEGER);

create table "Count" ("id" BIGSERIAL NOT NULL PRIMARY KEY, "product_id" INT NOT NULL, "period_id" INT NOT NULL , "remaining_quantity" INTEGER, "date" TIMESTAMP );

# --- !Downs
;
drop table "suppliers";

drop table "Product";

drop table "Period";

drop table "Count"
