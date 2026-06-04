ALTER TABLE purchases
DROP COLUMN collections_id,
DROP COLUMN snapshot_price;

ALTER TABLE purchases
    ADD COLUMN stripe_session_id text;

ALTER TABLE purchases
    ALTER COLUMN stripe_payment_intent_id DROP NOT NULL;

ALTER TABLE purchases
    ADD CONSTRAINT purchases_stripe_session_id_key UNIQUE (stripe_session_id);

UPDATE purchases SET stripe_session_id = 'legacy_' || id WHERE stripe_session_id IS NULL;

ALTER TABLE purchases
    ALTER COLUMN stripe_session_id SET NOT NULL;


CREATE TABLE purchase_items (
                                id bigserial NOT NULL,
                                purchase_id int8 NOT NULL,
                                collection_id int8 NOT NULL,
                                snapshot_price decimal(10,2) NOT NULL,
                                created_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT purchase_items_pk PRIMARY KEY (id),
                                CONSTRAINT fk_purchase_items_purchase
                                    FOREIGN KEY (purchase_id)
                                        REFERENCES purchases (id)
                                        ON DELETE CASCADE
);

CREATE INDEX idx_purchase_items_purchase_id ON purchase_items(purchase_id);
CREATE INDEX idx_purchases_session_id ON purchases(stripe_session_id);