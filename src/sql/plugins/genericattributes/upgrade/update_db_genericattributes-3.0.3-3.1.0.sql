-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-3.0.3-3.1.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'genatt_entry_type' AND column_name = 'id_type_parent'
ALTER TABLE genatt_entry_type ADD COLUMN id_type_parent int default NULL;
ALTER TABLE genatt_entry_type ADD COLUMN regex_pattern varchar(1024) default NULL;
ALTER TABLE genatt_entry_type ADD COLUMN regex_error_message varchar(512) default NULL;

-- Each column gets its own guard: a failure between two ALTER of the changeset above leaves the next columns to these ones
-- changeset genericattributes:update_db_genericattributes-3.0.3-3.1.0-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'genatt_entry_type' AND column_name = 'regex_pattern'
ALTER TABLE genatt_entry_type ADD COLUMN regex_pattern varchar(1024) default NULL;

-- changeset genericattributes:update_db_genericattributes-3.0.3-3.1.0-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'genatt_entry_type' AND column_name = 'regex_error_message'
ALTER TABLE genatt_entry_type ADD COLUMN regex_error_message varchar(512) default NULL;
