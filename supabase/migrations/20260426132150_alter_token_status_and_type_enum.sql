ALTER TYPE token_status ADD VALUE IF NOT EXISTS 'pending';
ALTER TYPE token_status ADD VALUE IF NOT EXISTS 'verified';
ALTER TYPE token_status ADD VALUE IF NOT EXISTS 'expired';

ALTER TYPE token_type ADD VALUE IF NOT EXISTS 'change_email';
ALTER TYPE token_type ADD VALUE IF NOT EXISTS 'change_password';
