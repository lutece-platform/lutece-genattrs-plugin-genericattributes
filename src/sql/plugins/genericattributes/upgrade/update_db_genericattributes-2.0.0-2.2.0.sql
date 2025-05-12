--liquibase formatted sql
--changeset genericattributes:update_db_genericattributes-2.0.0-2.2.0.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE genatt_entry DROP COLUMN is_editable_back;
UPDATE genatt_entry_type SET icon_name='sticky-note' WHERE id_type=107;