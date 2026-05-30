ALTER TABLE images
    ADD COLUMN collections_id BIGINT;

ALTER TABLE images
    ADD CONSTRAINT fk_images_collection
        FOREIGN KEY (collections_id) REFERENCES collections(id) ON DELETE CASCADE;