UPDATE email_verification_token SET status = 'pending' WHERE status = 'PENDING';
ALTER TABLE email_verification_token ALTER COLUMN status SET DEFAULT 'pending';