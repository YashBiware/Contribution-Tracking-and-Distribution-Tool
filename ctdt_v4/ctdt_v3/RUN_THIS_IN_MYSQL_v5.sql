USE ctdt_db;

-- Add directory_path if not already present (v2.0 may have added it manually)
ALTER TABLE project ADD COLUMN IF NOT EXISTS directory_path VARCHAR(500) NULL;

-- Add join token for project sharing (auto-generated UUID)
ALTER TABLE project ADD COLUMN IF NOT EXISTS join_token VARCHAR(36) NULL;
UPDATE project SET join_token = UUID() WHERE join_token IS NULL;
ALTER TABLE project MODIFY COLUMN join_token VARCHAR(36) NOT NULL;
ALTER TABLE project ADD UNIQUE INDEX uq_project_join_token (join_token);

-- Enable cascade deletes so we can delete a project cleanly
ALTER TABLE contribution_log
  DROP FOREIGN KEY fk_log_project,
  ADD CONSTRAINT fk_log_project FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE;

ALTER TABLE contribution_log
  DROP FOREIGN KEY fk_log_contributor,
  ADD CONSTRAINT fk_log_contributor FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id) ON DELETE CASCADE;

ALTER TABLE contribution_result
  DROP FOREIGN KEY fk_result_project,
  ADD CONSTRAINT fk_result_project FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE;

ALTER TABLE contribution_result
  DROP FOREIGN KEY fk_result_contributor,
  ADD CONSTRAINT fk_result_contributor FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id) ON DELETE CASCADE;

ALTER TABLE recalculation_history
  DROP FOREIGN KEY fk_history_project,
  ADD CONSTRAINT fk_history_project FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE;

ALTER TABLE contributor
  DROP FOREIGN KEY fk_contributor_project,
  ADD CONSTRAINT fk_contributor_project FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE;
