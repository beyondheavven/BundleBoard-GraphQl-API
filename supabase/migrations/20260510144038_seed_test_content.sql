INSERT INTO images (file_name, file_path, mime_type, width, height, file_size) VALUES
                                                                                   ('Business Card Mockups', 'https://lemevepzkbfxkunmrgor.supabase.co/storage/v1/object/public/previews/Business%20Card%20Mockups.jpg', 'jpeg', 1000, 1000, 450000),
                                                                                   ('Press Wall Banner', 'https://lemevepzkbfxkunmrgor.supabase.co/storage/v1/object/public/previews/Press%20Wall%20Banner%20Mockups.jpg', 'jpeg', 1000, 1000, 520000),
                                                                                   ('Embroidery Mockup', 'https://lemevepzkbfxkunmrgor.supabase.co/storage/v1/object/public/previews/White%20Stitching%20Embroidery%20Mockup.jpg', 'jpeg', 1000, 1000, 380000),
                                                                                   ('Y2K Chrome Effect', 'https://lemevepzkbfxkunmrgor.supabase.co/storage/v1/object/public/previews/Y2K%20Chrome%20Effect.jpg', 'jpeg', 1000, 1000, 610000);

INSERT INTO collections (name, description, authors_id, price, video_tutorial_url, project_file_id, preview_image_id)
VALUES
    (
        'Premium Business Card Mockups',
        'Professional 3D mockups for branding and identity design.',
        3, 15.00, 'https://youtube.com/watch?v=mockup1', 2,
        (SELECT id FROM images WHERE file_name = 'Business Card Mockups' LIMIT 1)
    ),
(
    'Y2K Chrome Text Effect',
    'Editable Photoshop text effect in futuristic chrome style.',
    3, 12.00, 'https://youtube.com/watch?v=chrome1', 2,
    (SELECT id FROM images WHERE file_name = 'Y2K Chrome Effect' LIMIT 1)
),
(
    'Press Wall Banner Set',
    'Realistic exhibition stand mockups for events and marketing.',
    3, 18.50, 'https://youtube.com/watch?v=banner1', 2,
    (SELECT id FROM images WHERE file_name = 'Press Wall Banner' LIMIT 1)
),
(
    'Realistic Embroidery Effect',
    'Stitching and patch mockup to simulate realistic fabric textures.',
    3, 14.00, 'https://youtube.com/watch?v=stitch1', 2,
    (SELECT id FROM images WHERE file_name = 'Embroidery Mockup' LIMIT 1)
);

INSERT INTO collection_tags (tags_id, collections_id)
SELECT id, (SELECT id FROM collections WHERE name = 'Premium Business Card Mockups') FROM tags WHERE name = 'UI Kit';

INSERT INTO collection_tags (tags_id, collections_id)
SELECT id, (SELECT id FROM collections WHERE name = 'Y2K Chrome Text Effect') FROM tags WHERE name = 'Textures';