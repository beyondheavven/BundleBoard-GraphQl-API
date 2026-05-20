-- INDEXES FOR FOREIGN KEYS

CREATE INDEX idx_collections_name ON collections (name);
CREATE INDEX idx_collections_price ON collections (price);
CREATE INDEX idx_collections_authors_id ON collections (authors_id);
CREATE INDEX idx_collection_tags_collections_id ON collection_tags (collections_id);
CREATE INDEX idx_tags_name ON tags (name);
CREATE INDEX idx_purchases_clients_id ON purchases (clients_id);
CREATE INDEX idx_purchases_created_at ON purchases (created_at DESC);
CREATE INDEX idx_carts_clients_id ON carts (clients_id);
CREATE INDEX idx_refresh_token_token ON refresh_token (token);
CREATE INDEX idx_refresh_token_users_id ON refresh_token (users_id);
CREATE INDEX idx_collections_project_file ON collections (project_file_id);
CREATE INDEX idx_collections_preview_image ON collections (preview_image_id);
CREATE INDEX idx_refresh_token_expires ON refresh_token(expiration_time);