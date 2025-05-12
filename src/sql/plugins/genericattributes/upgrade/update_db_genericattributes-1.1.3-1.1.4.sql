--liquibase formatted sql
--changeset genericattributes:update_db_genericattributes-1.1.3-1.1.4.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE genatt_field ADD `code` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL;
ALTER TABLE genatt_entry ADD `code` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL;
