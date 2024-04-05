INSERT INTO genatt_field (id_entry, code, VALUE, title)
	SELECT e.id_entry, 'disabled', 'false', ''
	from genatt_entry e ;
