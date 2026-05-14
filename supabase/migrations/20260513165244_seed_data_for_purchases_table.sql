
INSERT INTO purchases (collections_id, clients_id, stripe_payment_intent_id, amount, currency, status, snapshot_price, created_at, updated_at)
VALUES
    (3, 7, 'pi_ivan_fire_' || gen_random_uuid(), 49.99, 'USD', 'succeeded', 49.99, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days');


INSERT INTO purchases (collections_id, clients_id, stripe_payment_intent_id, amount, currency, status, snapshot_price, created_at, updated_at)
VALUES
    (3, 8, 'pi_kiryl_vfx_' || gen_random_uuid(), 49.99, 'USD', 'succeeded', 49.99, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
    (4, 9, 'pi_kiryl_hud_' || gen_random_uuid(), 25.00, 'USD', 'succeeded', 25.00, NOW(), NOW());

INSERT INTO purchases (collections_id, clients_id, stripe_payment_intent_id, amount, currency, status, snapshot_price, created_at, updated_at)
VALUES
    (5, 8, 'pi_newuser_fire_' || gen_random_uuid(), 49.99, 'USD', 'succeeded', 49.99, NOW() - INTERVAL '5 hours', NOW() - INTERVAL '5 hours');