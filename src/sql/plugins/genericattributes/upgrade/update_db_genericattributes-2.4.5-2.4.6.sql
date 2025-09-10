-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-2.4.5-2.4.6.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE genatt_response CHANGE COLUMN id_file file_key VARCHAR(255);
ALTER TABLE genatt_response ADD COLUMN file_store VARCHAR(255) DEFAULT NULL AFTER file_key;

INSERT INTO genatt_field ( id_entry, title, code, VALUE, default_value )
SELECT e.id_entry, null, 'default_today_date', 'false', 0
FROM genatt_entry e
         INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE e.resource_type = 'FORMS_FORM'
  AND t.class_name = 'forms.entryTypeDate';