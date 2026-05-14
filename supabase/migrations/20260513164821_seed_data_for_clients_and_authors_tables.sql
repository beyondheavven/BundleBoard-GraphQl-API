INSERT INTO clients (users_id, newsletter_subscribed, preferred_language) VALUES
(7, true, 'en'),
(8, true, 'en'),
(9, false, 'en'),
(39, true, 'ru'),
(41, true, 'ru')
ON CONFLICT (users_id) DO NOTHING;

INSERT INTO authors (users_id, bio, social_links, rating, total_sales, stripe_account_id) VALUES
(39, 'Motion Designer & VFX Artist', '{"web": "kurzau.art"}', 5.0, 0, 'acct_kurzau_39'),
(41, 'Digital Content Creator', '{}', 5.0, 0, 'acct_newuser_41')
ON CONFLICT (users_id) DO NOTHING;