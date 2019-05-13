
-- 
-- Add a new table for the mapping of automatic file reading
-- 
CREATE TABLE genatt_mapping_file_reading (
  id_mapping int AUTO_INCREMENT,
  id_step INT DEFAULT 0 NOT NULL,
  id_question INT DEFAULT 0 NOT NULL,
  question_title varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  id_field_ocr INT DEFAULT 0 NOT NULL,
  field_ocr_title varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  PRIMARY KEY (id_mapping)
);

ALTER TABLE genatt_entry MODIFY COLUMN code varchar(100) default NULL; 
ALTER TABLE genatt_field MODIFY COLUMN code varchar(100) default NULL; 

CREATE INDEX index_genatt_code ON genatt_entry ( code);

