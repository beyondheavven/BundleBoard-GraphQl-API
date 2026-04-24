CREATE TYPE token_type AS ENUM ('verify_email', 'reset_password');

CREATE TABLE email_verification_token (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  token TEXT NOT NULL,
  type token_type NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE

);

CREATE INDEX idx_email_verification_tokens_token ON email_verification_token(token);
CREATE INDEX idx_email_verification_tokens_user_id ON email_verification_token(user_id);