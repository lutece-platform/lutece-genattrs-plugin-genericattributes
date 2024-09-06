ALTER TABLE genatt_response CHANGE COLUMN id_file file_key VARCHAR(255);
ALTER TABLE genatt_response ADD COLUMN file_store VARCHAR(255) DEFAULT NULL AFTER file_key;