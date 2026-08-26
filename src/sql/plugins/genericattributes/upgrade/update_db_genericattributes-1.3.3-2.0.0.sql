-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-1.3.3-2.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE LOWER(table_name) = 'genatt_entry' AND LOWER(column_name) = 'num_row'
-- LUT-33259 : 2.0.0 fields model : array/confirm_field/width/height/max_size recovery, then the column drops formerly commented out.
-- Formerly "To use on an empty database only" with every statement commented out, the migration being left to
-- dependent plugins (forms, appointment, announce...) which each shipped a copy of it. The precondition makes it
-- MARK_RAN on databases already migrated by a dependent plugin.
INSERT INTO genatt_field (id_entry, code, value)
    SELECT id_entry, 'array_row', num_row FROM genatt_entry WHERE num_row > 0;
INSERT INTO genatt_field (id_entry, code, value)
    SELECT id_entry, 'array_column', num_column FROM genatt_entry WHERE num_column > 0;
ALTER TABLE genatt_entry DROP COLUMN num_row;
ALTER TABLE genatt_entry DROP COLUMN num_column;
INSERT INTO genatt_field (id_entry, code, value, title)
    SELECT e.id_entry, 'confirm_field', CASE e.confirm_field WHEN 1 THEN 'true' ELSE 'false' END, e.confirm_field_title
    FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE t.class_name LIKE '%.entryTypeText';
ALTER TABLE genatt_entry DROP COLUMN confirm_field;
ALTER TABLE genatt_entry DROP COLUMN confirm_field_title;
INSERT INTO genatt_field (id_entry, code, value)
    SELECT id_entry, 'width', width FROM genatt_field WHERE width > 0 AND code NOT IN ('file_config', 'user_config');
INSERT INTO genatt_field (id_entry, code, value)
    SELECT id_entry, 'height', height FROM genatt_field WHERE height > 0;
INSERT INTO genatt_field (id_entry, code, value)
    SELECT id_entry, 'max_size', max_size_enter FROM genatt_field WHERE max_size_enter IS NOT NULL AND max_size_enter != 0;
DELETE FROM genatt_field WHERE code = 'file_config';
DELETE FROM genatt_field WHERE code = 'user_config';
ALTER TABLE genatt_field DROP COLUMN width;
ALTER TABLE genatt_field DROP COLUMN height;
ALTER TABLE genatt_field DROP COLUMN max_size_enter;
ALTER TABLE genatt_entry DROP COLUMN map_provider;
ALTER TABLE genatt_field DROP COLUMN image_type;
ALTER TABLE genatt_entry DROP COLUMN is_role_associated;
ALTER TABLE genatt_field DROP COLUMN role_key;
ALTER TABLE genatt_entry MODIFY COLUMN id_entry INT AUTO_INCREMENT NOT NULL;
