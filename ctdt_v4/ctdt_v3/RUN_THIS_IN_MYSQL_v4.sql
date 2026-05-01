USE ctdt_db;
ALTER TABLE contributor ADD COLUMN username VARCHAR(100) NULL AFTER contributor_name;
ALTER TABLE contributor ADD COLUMN password_hash VARCHAR(255) NULL AFTER username;
