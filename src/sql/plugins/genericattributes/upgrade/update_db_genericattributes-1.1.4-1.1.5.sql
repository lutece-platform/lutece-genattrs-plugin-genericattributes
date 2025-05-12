--liquibase formatted sql
--changeset genericattributes:update_db_genericattributes-1.1.4-1.1.5.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE genatt_entry ADD COLUMN  is_only_display_back smallint DEFAULT '0';
