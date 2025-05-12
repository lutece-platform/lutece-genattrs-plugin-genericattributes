--liquibase formatted sql
--changeset genericattributes:update_db_genericattributes-1.2.1-1.3.0.sql
--preconditions onFail:MARK_RAN onError:WARN
-- 
-- Add a new column for the iteration number in genatt_response table
-- 
ALTER TABLE genatt_response ADD COLUMN iteration_number int default -1 AFTER id_entry;

-- 
-- Trim the title of all existing entry
-- 
UPDATE genatt_entry SET title = TRIM(title);
