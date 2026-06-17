SET session_replication_role = 'replica';

TRUNCATE TABLE
    authors,
    clients,
    collection_images,
    collection_likes,
    collection_tags,
    collections,
    purchase_items,
    purchases,
    refresh_token,
    password_reset_tokens,
    email_verification_token,
    tags,
    comments,
    media_resources,
    users
    RESTART IDENTITY CASCADE;

SET session_replication_role = 'origin';