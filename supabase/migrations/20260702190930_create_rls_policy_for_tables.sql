-- 1. authors
ALTER TABLE public.authors ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.authors FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 2. clients
ALTER TABLE public.clients ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.clients FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 3. collection_images
ALTER TABLE public.collection_images ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.collection_images FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 4. collection_likes
ALTER TABLE public.collection_likes ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.collection_likes FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 5. collection_tags
ALTER TABLE public.collection_tags ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.collection_tags FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 6. collections
ALTER TABLE public.collections ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.collections FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 7. comments
ALTER TABLE public.comments ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.comments FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 8. email_verification_token
ALTER TABLE public.email_verification_token ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.email_verification_token FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 9. images
ALTER TABLE public.images ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.images FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 10. media_resources
ALTER TABLE public.media_resources ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.media_resources FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 11. password_reset_tokens
ALTER TABLE public.password_reset_tokens ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.password_reset_tokens FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 12. purchase_items
ALTER TABLE public.purchase_items ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.purchase_items FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 13. purchases
ALTER TABLE public.purchases ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.purchases FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 14. refresh_token
ALTER TABLE public.refresh_token ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.refresh_token FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 15. tags
ALTER TABLE public.tags ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.tags FOR ALL TO postgres USING (true) WITH CHECK (true);

-- 16. users
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.users FOR ALL TO postgres USING (true) WITH CHECK (true);