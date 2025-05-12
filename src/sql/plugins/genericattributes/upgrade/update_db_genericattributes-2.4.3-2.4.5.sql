--liquibase formatted sql
--changeset genericattributes:update_db_genericattributes-2.4.3-2.4.5.sql
--preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_field (id_entry, code, VALUE, title)
	SELECT e.id_entry, 'disabled', 'false', ''
	from genatt_entry e ;

UPDATE core_file f SET f.origin = 'defaultDatabaseFileStoreProvider'
WHERE f.origin is null
AND exists (SELECT 1 FROM genatt_response r WHERE r.id_file = f.id_file)
;

ALTER TABLE genatt_entry_type MODIFY COLUMN id_type int AUTO_INCREMENT NOT NULL;