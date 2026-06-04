ALTER TABLE purchases DROP CONSTRAINT IF EXISTS purchases_clients;

ALTER TABLE purchases
    ADD CONSTRAINT purchases_users_fk
        FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE purchases
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;