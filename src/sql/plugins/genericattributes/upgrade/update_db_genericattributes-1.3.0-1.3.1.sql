--liquibase formatted sql
--changeset genericattributes:update_db_genericattributes-1.3.0-1.3.1.sql
--preconditions onFail:MARK_RAN onError:WARN
-- 
-- Add a new column for the EntryType icon name in genatt_entry_type table
-- 
ALTER TABLE genatt_entry_type ADD COLUMN icon_name varchar(255) AFTER class_name;
--
-- Add a new column for the editable back in genatt_entry table
--
ALTER TABLE genatt_entry ADD COLUMN (is_editable_back smallint DEFAULT '0');

ALTER TABLE genatt_entry ADD COLUMN (is_indexed SMALLINT default 0 NOT NULL);