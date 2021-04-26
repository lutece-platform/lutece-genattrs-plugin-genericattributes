ALTER TABLE genatt_entry DROP COLUMN is_editable_back;
UPDATE genatt_entry_type SET icon_name='sticky-note' WHERE id_type=107;