ALTER TABLE users ALTER COLUMN avatar_url DROP NOT NULL;

ALTER TABLE users
ALTER COLUMN roles TYPE VARCHAR(32)[]
    USING roles::text[]::VARCHAR(32)[];

ALTER TABLE users
ALTER COLUMN status TYPE VARCHAR(32)
    USING status::text::VARCHAR(32);

ALTER TABLE users ADD CONSTRAINT user_status_check CHECK (status IN ('active', 'inactive', 'banned'));

DROP TYPE user_role CASCADE;
DROP TYPE user_status CASCADE;
