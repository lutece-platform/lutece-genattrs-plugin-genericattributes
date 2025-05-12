--liquibase formatted sql
--changeset genericattributes:update_db_genericattributes-2.4.2-2.4.3.sql
--preconditions onFail:MARK_RAN onError:WARN
alter table genatt_field add column id_file_key VARCHAR(50) default null;