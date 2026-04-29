ALTER TABLE email_verification_token
    ADD COLUMN attempt_count INT NOT NULL DEFAULT 0,
    ADD COLUMN blocked_until TIMESTAMPTZ,
    ADD COLUMN resend_count INT NOT NULL DEFAULT 0;