-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-1.3.3-2.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- To use on an empty database only
-- for a full updates, see plugins depending on genatt (forms, ticketing...)

-- ALTER TABLE genatt_entry DROP COLUMN num_row;
-- ALTER TABLE genatt_entry DROP COLUMN num_column;

-- ALTER TABLE genatt_entry DROP COLUMN confirm_field;
-- ALTER TABLE genatt_entry DROP COLUMN confirm_field_title

-- ALTER TABLE genatt_field DROP COLUMN width;

-- ALTER TABLE genatt_field DROP COLUMN height;

-- ALTER TABLE genatt_field DROP COLUMN max_size_enter;

-- ALTER TABLE genatt_entry DROP COLUMN map_provider;

-- ALTER TABLE genatt_field DROP COLUMN image_type;

-- ALTER TABLE genatt_entry DROP COLUMN is_role_associated;
-- ALTER TABLE genatt_field DROP COLUMN role_key;
-- ALTER TABLE genatt_entry modify COLUMN id_entry int AUTO_INCREMENT NOT NULL;