ALTER TABLE collection_tags DROP CONSTRAINT IF EXISTS collection_tags_collections_id_fkey;

ALTER TABLE collection_tags
    ADD CONSTRAINT collection_tags_collections_id_fkey
        FOREIGN KEY (collections_id) REFERENCES collections(id) ON DELETE CASCADE;