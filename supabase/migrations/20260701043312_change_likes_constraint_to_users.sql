ALTER TABLE collection_likes
DROP CONSTRAINT IF EXISTS fk_like_author;

ALTER TABLE collection_likes
    ADD CONSTRAINT fk_like_user
        FOREIGN KEY (user_id)
            REFERENCES users (id) ON DELETE CASCADE;