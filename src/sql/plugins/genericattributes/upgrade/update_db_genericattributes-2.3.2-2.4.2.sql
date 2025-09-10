-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-2.3.2-2.4.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE genatt_response ADD COLUMN sort_order int default 0;
