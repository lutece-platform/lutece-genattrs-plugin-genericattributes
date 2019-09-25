-- To use on an empty database only
-- for a full updates, see plugins depending on genatt (forms, ticketing...)

--ALTER TABLE genatt_entry CHANGE COLUMN is_shown_in_completeness used_in_correct_form_response SMALLINT DEFAULT '0';

--ALTER TABLE genatt_field MODIFY id_field INT AUTO_INCREMENT;

--ALTER TABLE genatt_entry DROP COLUMN used_in_correct_form_response;