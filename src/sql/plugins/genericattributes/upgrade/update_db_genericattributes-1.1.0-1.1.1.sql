-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-1.1.0-1.1.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE genatt_entry ADD COLUMN num_row smallint default 0;
ALTER TABLE genatt_entry ADD COLUMN num_column smallint default 0;