-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-3.0.2-3.0.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND LOWER(table_name) = 'genatt_entry_numbering'
-- Counter of the numbering entries, shared by the instances of the application (it was held in the memory of each instance)
CREATE TABLE genatt_entry_numbering (
	id_entry int NOT NULL,
	counter_value int default 0 NOT NULL,
	PRIMARY KEY (id_entry)
);
