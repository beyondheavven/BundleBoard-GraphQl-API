ALTER TABLE collections
    ADD COLUMN external_link VARCHAR(512) DEFAULT NULL;

ALTER TABLE collections
    ALTER COLUMN project_file_id DROP NOT NULL;