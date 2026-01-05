-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-2.4.6-3.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url='ti ti-input-check' WHERE id_right='ENTRY_TYPE_MANAGEMENT';