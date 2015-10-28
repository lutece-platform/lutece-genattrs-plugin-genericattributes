ALTER TABLE genatt_field ADD `role_key` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL;
ALTER TABLE genatt_entry ADD `is_role_associated` smallint(1) DEFAULT '0';