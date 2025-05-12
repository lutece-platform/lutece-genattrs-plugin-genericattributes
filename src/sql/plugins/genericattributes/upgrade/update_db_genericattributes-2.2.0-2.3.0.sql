--liquibase formatted sql
--changeset genericattributes:update_db_genericattributes-2.2.0-2.3.0.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE genatt_entry_type ADD COLUMN display_order int default 0;
ALTER TABLE genatt_entry_type ADD COLUMN inactive int default 0;

DELETE FROM core_admin_right WHERE id_right = 'ENTRY_TYPE_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('ENTRY_TYPE_MANAGEMENT','genericattributes.adminFeature.manageEntryType.name',1,'jsp/admin/plugins/genericattributes/ManageEntryType.jsp','genericattributes.adminFeature.manageEntryType.description',0,'genericattributes',NULL,NULL,NULL,5);

INSERT INTO core_user_right (id_right,id_user) VALUES ('ENTRY_TYPE_MANAGEMENT',1);

