DO $$
DECLARE
table_name text;
BEGIN
FOR table_name IN
SELECT tablename
FROM pg_tables
WHERE schemaname = 'public'
    LOOP
        EXECUTE format('DROP POLICY IF EXISTS "allow_backend_access" ON public.%I', table_name);
EXECUTE format('CREATE POLICY "allow_backend_access" ON public.%I FOR ALL TO postgres USING (true) WITH CHECK (true)', table_name);
END LOOP;
END $$;