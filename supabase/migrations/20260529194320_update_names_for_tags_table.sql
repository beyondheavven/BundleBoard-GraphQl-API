TRUNCATE TABLE collection_tags CASCADE;

TRUNCATE TABLE tags RESTART IDENTITY CASCADE;

INSERT INTO tags (name) VALUES
                            ('brushes'),
                            ('fonts'),
                            ('gradients'),
                            ('graphics'),
                            ('textures'),
                            ('mockups');