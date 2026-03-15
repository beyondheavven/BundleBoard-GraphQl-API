-- 0.
TRUNCATE users, authors, clients, tags, images, media_resources, collections, collection_tags, carts, purchases, refresh_token CASCADE;

-- 1. USERS
INSERT INTO users (username, email, roles, password_hash, avatar_url, status, last_login_at, created_at) VALUES
                                                                                                             ('admin_max', 'admin@platform.com', '{admin}', '3700adf1f25fab8202c1343c4b0b4e3fec706d57cad574086467b8b3ddf273ec', 'https://cdn.com/avatars/1.jpg', 'active', NOW(), NOW()),
                                                                                                             ('sergey_vfx', 'sergey@vfx.com', '{author}', '3700adf1f25fab8202c1343c4b0b4e3fec706d57cad574086467b8b3ddf273ec', 'https://cdn.com/avatars/2.jpg', 'active', NOW(), NOW()),
                                                                                                             ('elena_design', 'elena@design.com', '{author}', '3700adf1f25fab8202c1343c4b0b4e3fec706d57cad574086467b8b3ddf273ec', 'https://cdn.com/avatars/3.jpg', 'active', NOW(), NOW()),
                                                                                                             ('ivan_buyer', 'ivan@mail.com', '{client}', '3700adf1f25fab8202c1343c4b0b4e3fec706d57cad574086467b8b3ddf273ec', 'https://cdn.com/avatars/4.jpg', 'active', NOW(), NOW()),
                                                                                                             ('olga_creative', 'olga@mail.com', '{client}', '3700adf1f25fab8202c1343c4b0b4e3fec706d57cad574086467b8b3ddf273ec', 'https://cdn.com/avatars/5.jpg', 'inactive', NULL, NOW());

-- 2. AUTHORS
INSERT INTO authors (users_id, bio, social_links, rating, total_sales, stripe_account_id) VALUES
                                                                                              (2, 'VFX Artist with 10+ years experience in Hollywood.', '{"twitter": "@sergey_vfx", "web": "sergey.art"}', 4.95, 125, 'acct_1NfG2jLp01'),
                                                                                              (3, 'UI/UX Designer and 3D illustrator.', '{"instagram": "@elena_3d", "behance": "elenadesign"}', 4.80, 45, 'acct_1NfG3kMp02');

-- 3. CLIENTS
INSERT INTO clients (users_id, newsletter_subscribed, preferred_language) VALUES
                                                                              (4, true, 'ru'),
                                                                              (5, false, 'en');

-- 4. TAGS
INSERT INTO tags (name) VALUES
                            ('3D Modeling'), ('VFX'), ('After Effects'), ('Textures'), ('Blueprints'), ('UI Kit');

-- 5. IMAGES
INSERT INTO images (file_name, file_path, mime_type, width, height, file_size) VALUES
                                                                                   ('fire_explosion_thumb.jpg', '/previews/2026/01.jpg', 'image/jpeg', 1280, 720, 450000),
                                                                                   ('sci_fi_ui_thumb.png', '/previews/2026/02.png', 'image/png', 1920, 1080, 1200000);

-- 6. MEDIA_RESOURCES
INSERT INTO media_resources (file_name, file_path, file_type, mime_type, provider, file_size) VALUES
                                                                                                  ('explosion_project.zip', '/vault/vfx/01.zip', 'archive', 'application/zip', 'local', 524288000),
                                                                                                  ('ui_elements.rar', '/vault/design/02.rar', 'archive', 'application/rar', 'google_drive', 104857600),
                                                                                                  ('license_agreement.pdf', '/docs/legal/license.pdf', 'document', 'application/pdf', 'local', 150000);

-- 7. COLLECTIONS
INSERT INTO collections (name, description, authors_id, price, video_tutorial_url, project_file_id, preview_image_id) VALUES
                                                                                                                          ('Cinematic Fire VFX', 'Professional fire and explosion assets for film.', 2, 49.99, 'https://youtube.com/v/vfx1', 1, 1),
                                                                                                                          ('Futuristic HUD Kit', 'Complete UI elements for game development.', 3, 25.00, 'https://youtube.com/v/ui2', 2, 2);

-- 8. COLLECTIONS & TAGS
INSERT INTO collection_tags (tags_id, collections_id) VALUES
                                                          (2, 1), (3, 1),
                                                          (1, 2), (6, 2);

-- 9. CARTS
INSERT INTO carts (clients_id, collections_id, quantity) VALUES
    (4, 1, 1);

-- 10. PURCHASES
INSERT INTO purchases (collections_id, clients_id, stripe_payment_intent_id, amount, currency, status, snapshot_price, created_at, updated_at) VALUES
                                                                                                                                                   (2, 4, 'pi_3O1abc123', 25.00, 'USD', 'succeeded', 25.00, '2026-02-28 10:00:00+03', '2026-02-28 10:05:00+03'),
                                                                                                                                                   (1, 5, 'pi_3O1xyz789', 49.99, 'USD', 'pending', 49.99, NOW(), NOW());

-- 11. REFRESH_TOKEN (issued_at & expiration_time)
INSERT INTO refresh_token (users_id, token, issued_at, expiration_time) VALUES
                                                                       (1, 'long-random-jwt-token-string-for-admin', NOW(), NOW() + INTERVAL '30 days'),
                                                                       (4, 'long-random-jwt-token-string-for-ivan', NOW(), NOW() + INTERVAL '30 days');