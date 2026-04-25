CREATE TYPE token_status AS ENUM ('PENDING', 'VERIFIED', 'EXPIRED');

ALTER TABLE email_verification_token
    ADD COLUMN status token_status NOT NULL DEFAULT 'PENDING';