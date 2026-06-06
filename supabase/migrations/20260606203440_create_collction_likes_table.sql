CREATE TABLE collection_likes (
            id BIGSERIAL PRIMARY KEY,
            collection_id BIGINT NOT NULL,
            author_id BIGINT NOT NULL,
            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
            CONSTRAINT fk_like_collection FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE,
            CONSTRAINT fk_like_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE,
            CONSTRAINT uq_collection_author_like UNIQUE (collection_id, author_id)
);