ALTER TABLE collections ADD COLUMN slug text;

UPDATE collections
SET slug = regexp_replace(lower(trim(name)), '[^a-z0-9]+', '-', 'g');

UPDATE collections
SET slug = trim(BOTH '-' FROM slug);

ALTER TABLE collections ALTER COLUMN slug SET NOT NULL;

CREATE UNIQUE INDEX collections_slug_unique ON collections (authors_id, slug);