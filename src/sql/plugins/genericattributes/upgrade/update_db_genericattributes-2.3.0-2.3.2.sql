-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-2.3.0-2.3.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS genatt_referenceitem_field;

CREATE TABLE genatt_referenceitem_field (
	id_field int default 0 NOT NULL,
	id_item int default 0 NOT NULL,
	PRIMARY KEY( id_field )
);

INSERT INTO genatt_field (id_entry, code, VALUE, title)
	SELECT e.id_entry, 'use_ref_list', 'false', '-1' 
	from genatt_entry e ;

INSERT INTO genatt_field (id_entry, code, VALUE, title)
	SELECT e.id_entry, 'anonymizable', 'false', '' 
	from genatt_entry e ;
