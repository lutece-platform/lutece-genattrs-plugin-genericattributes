-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-1.3.2-1.3.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE LOWER(table_name) = 'genatt_entry' AND LOWER(column_name) = 'is_shown_in_completeness'
-- LUT-33259 : 1.3.3 field-code model : generic data recovery keyed on the entry-type kind, then the DDL formerly commented out.
-- Formerly "To use on an empty database only" with every statement commented out, the migration being left to
-- dependent plugins (forms, appointment, announce...) which each shipped a copy of it. The precondition makes it
-- MARK_RAN on databases already migrated by a dependent plugin.
UPDATE genatt_field f SET f.code = 'default_date_value'
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE t.class_name LIKE '%.entryTypeDate' AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.code = f.title
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE t.class_name LIKE '%.entryTypeGeolocation' AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.title = NULL
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE t.class_name LIKE '%.entryTypeGeolocation' AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.value = f.title
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE t.class_name LIKE '%.entryTypeNumbering' AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.code = 'prefix'
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE t.class_name LIKE '%.entryTypeNumbering' AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.title = NULL
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE t.class_name LIKE '%.entryTypeNumbering' AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.code = f.title
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE (t.class_name LIKE '%.entryTypeFile' OR t.class_name LIKE '%.entryTypeImage' OR t.class_name LIKE '%.entryTypeAutomaticFileReading') AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.code = 'file_config'
WHERE f.code IS NULL AND f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE (t.class_name LIKE '%.entryTypeFile' OR t.class_name LIKE '%.entryTypeImage' OR t.class_name LIKE '%.entryTypeAutomaticFileReading') AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.title = NULL
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE (t.class_name LIKE '%.entryTypeFile' OR t.class_name LIKE '%.entryTypeImage' OR t.class_name LIKE '%.entryTypeAutomaticFileReading') AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.code = 'answer_choice'
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE (t.class_name LIKE '%.entryTypeSelect' OR t.class_name LIKE '%.entryTypeRadioButton' OR t.class_name LIKE '%.entryTypeCheckBox') AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.code = 'array_cell'
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE t.class_name LIKE '%.entryTypeArray' AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.code = 'text_config'
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE (t.class_name LIKE '%.entryTypeText' OR t.class_name LIKE '%.entryTypeTextArea' OR t.class_name LIKE '%.entryTypePhone') AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.code = 'attribute_name'
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE t.class_name LIKE '%.entryTypeSession' AND e.id_entry = f.id_entry);
UPDATE genatt_field f SET f.code = 'user_config'
WHERE f.id_entry IN (SELECT e.id_entry FROM genatt_entry e INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
    WHERE t.class_name LIKE '%.entryTypeMyLuteceUser' AND e.id_entry = f.id_entry);
UPDATE genatt_entry e SET e.is_only_display_back = '1'
WHERE e.id_entry IN (SELECT f.id_entry FROM genatt_field f WHERE f.code = 'only_display_in_back' AND f.value = '1');
DELETE FROM genatt_field WHERE code = 'only_display_in_back';
ALTER TABLE genatt_field MODIFY id_field INT AUTO_INCREMENT;
INSERT INTO genatt_field (id_entry, code, value)
    SELECT id_entry, 'used_in_correct_form_response', CASE is_shown_in_completeness WHEN 1 THEN 'true' ELSE 'false' END FROM genatt_entry;
ALTER TABLE genatt_entry DROP COLUMN is_shown_in_completeness;
