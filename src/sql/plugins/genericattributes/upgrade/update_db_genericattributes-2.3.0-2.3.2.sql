DROP TABLE IF EXISTS genatt_referenceitem_field;

CREATE TABLE genatt_referenceitem_field (
	id_field int default 0 NOT NULL,
	id_item int default 0 NOT NULL,
	PRIMARY KEY( id_field )
);