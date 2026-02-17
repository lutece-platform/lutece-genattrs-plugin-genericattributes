-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-2.4.6-2.4.9.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE genatt_response MODIFY response_value LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL;
