DROP POLICY IF EXISTS "Allow authenticated uploads to vault" ON storage.objects;
DROP POLICY IF EXISTS "Allow authenticated select from vault" ON storage.objects;
DROP POLICY IF EXISTS "Allow authenticated updates to vault" ON storage.objects;
DROP POLICY IF EXISTS "Allow authenticated delete from vault" ON storage.objects;

DROP POLICY IF EXISTS "Allow authenticated uploads to previews" ON storage.objects;
DROP POLICY IF EXISTS "Allow public read access to previews" ON storage.objects;
DROP POLICY IF EXISTS "Allow authenticated updates to previews" ON storage.objects;
DROP POLICY IF EXISTS "Allow authenticated delete from previews" ON storage.objects;


CREATE POLICY "Allow authenticated uploads to vault"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'vault'::text);

CREATE POLICY "Allow authenticated select from vault"
ON storage.objects FOR SELECT
TO authenticated
USING (bucket_id = 'vault'::text);

CREATE POLICY "Allow authenticated updates to vault"
ON storage.objects FOR UPDATE
TO authenticated
USING (bucket_id = 'vault'::text);

CREATE POLICY "Allow authenticated delete from vault"
ON storage.objects FOR DELETE
TO authenticated
USING (bucket_id = 'vault'::text);


CREATE POLICY "Allow authenticated uploads to previews"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'previews'::text);


CREATE POLICY "Allow public read access to previews"
ON storage.objects FOR SELECT
TO public
USING (bucket_id = 'previews'::text);


CREATE POLICY "Allow authenticated updates to previews"
ON storage.objects FOR UPDATE
TO authenticated
USING (bucket_id = 'previews'::text);

CREATE POLICY "Allow authenticated delete from previews"
ON storage.objects FOR DELETE
TO authenticated
USING (bucket_id = 'previews'::text);