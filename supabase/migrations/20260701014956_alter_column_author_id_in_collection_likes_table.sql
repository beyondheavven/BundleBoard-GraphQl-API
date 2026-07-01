ALTER TABLE collection_likes DROP CONSTRAINT IF EXISTS fk_author;

ALTER TABLE collection_likes RENAME COLUMN author_id TO user_id;

ALTER TABLE collection_likes
    ADD CONSTRAINT fk_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;