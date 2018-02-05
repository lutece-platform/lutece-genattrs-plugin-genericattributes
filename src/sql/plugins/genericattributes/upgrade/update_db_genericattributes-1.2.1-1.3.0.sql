-- 
-- Add a new column for the iteration number in genatt_response table
-- 
ALTER TABLE genatt_response ADD COLUMN iteration_number int default -1 AFTER id_entry;

-- 
-- Trim the title of all existing entry
-- 
UPDATE genatt_entry SET title = TRIM(title);
