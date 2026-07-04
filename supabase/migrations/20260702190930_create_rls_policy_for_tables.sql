ALTER TABLE public.authors ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.authors FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.clients ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.clients FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.collection_images ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.collection_images FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.collection_likes ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.collection_likes FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.collection_tags ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.collection_tags FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.collections ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.collections FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.comments ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.comments FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.email_verification_token ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.email_verification_token FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.images ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.images FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.media_resources ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.media_resources FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.password_reset_tokens ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.password_reset_tokens FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.purchase_items ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.purchase_items FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.purchases ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.purchases FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.refresh_token ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.refresh_token FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.tags ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.tags FOR ALL TO postgres USING (true) WITH CHECK (true);

ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
CREATE POLICY "allow_backend_access" ON public.users FOR ALL TO postgres USING (true) WITH CHECK (true);