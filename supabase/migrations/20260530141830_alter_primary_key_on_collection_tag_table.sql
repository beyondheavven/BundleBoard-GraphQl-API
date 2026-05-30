
ALTER TABLE collection_tags ADD COLUMN id BIGSERIAL;

ALTER TABLE collection_tags DROP CONSTRAINT collection_tags_pk;
ALTER TABLE collection_tags ADD PRIMARY KEY (id);

ALTER TABLE collection_tags ADD CONSTRAINT collection_tags_tags_id_collections_id_key UNIQUE (tags_id, collections_id);