-- liquibase formatted sql
-- changeset genericattributes:init_db_core_genericattributes.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO core_datastore( entity_key, entity_value ) VALUES ('core.cache.status.EntryTypeServiceManagerCache.enabled', '1');

DELETE FROM core_admin_right WHERE id_right = 'ENTRY_TYPE_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('ENTRY_TYPE_MANAGEMENT','genericattributes.adminFeature.manageEntryType.name',1,'jsp/admin/plugins/genericattributes/ManageEntryType.jsp','genericattributes.adminFeature.manageEntryType.description',0,'genericattributes',NULL,NULL,NULL,5);

INSERT INTO core_user_right (id_right,id_user) VALUES ('ENTRY_TYPE_MANAGEMENT',1);
