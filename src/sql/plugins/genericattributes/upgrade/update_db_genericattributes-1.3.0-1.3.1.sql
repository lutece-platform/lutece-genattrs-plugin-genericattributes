-- 
-- Add a new column for the EntryType icon name in genatt_entry_type table
-- 
ALTER TABLE genatt_entry_type ADD COLUMN icon_name varchar(255) AFTER class_name;
--
-- Add a new column for the editable back in genatt_entry table
--
ALTER TABLE genatt_entry ADD COLUMN (is_editable_back smallint DEFAULT '0');