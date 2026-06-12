CREATE TABLE comments (
                id BIGSERIAL PRIMARY KEY,
                collection_id BIGINT NOT NULL,
                user_id BIGINT NOT NULL,
                content TEXT NOT NULL,
                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                CONSTRAINT fk_comments_collection FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE,
                CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_comments_collection_id ON comments(collection_id);