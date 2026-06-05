ALTER TABLE collections
DROP CONSTRAINT IF EXISTS collections_images;

ALTER TABLE collections
DROP COLUMN IF EXISTS preview_image_id;

ALTER TABLE images
DROP CONSTRAINT IF EXISTS fk_images_collection;

ALTER TABLE images
DROP COLUMN IF EXISTS collections_id;

CREATE TABLE collection_images (
                                   id BIGSERIAL PRIMARY KEY,
                                   collection_id BIGINT NOT NULL,
                                   image_id BIGINT NOT NULL,
                                   CONSTRAINT fk_collection FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE,
                                   CONSTRAINT fk_image FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE,
                                   CONSTRAINT uq_collection_image UNIQUE (collection_id, image_id)
);