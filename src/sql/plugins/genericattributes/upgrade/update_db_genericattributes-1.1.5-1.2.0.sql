--liquibase formatted sql
--changeset genericattributes:update_db_genericattributes-1.1.5-1.2.0.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE genatt_field ADD COLUMN  image_type varchar(50) DEFAULT NULL;