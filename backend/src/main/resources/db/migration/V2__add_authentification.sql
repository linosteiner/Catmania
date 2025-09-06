CREATE TABLE IF NOT EXISTS app_user
(
    id            BIGSERIAL PRIMARY KEY,
    username      TEXT UNIQUE NOT NULL,
    password_hash TEXT        NOT NULL,
    enabled       BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS app_user_role
(
    user_id BIGINT NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    role    TEXT   NOT NULL,
    PRIMARY KEY (user_id, role)
);

INSERT INTO app_user (username, password_hash, enabled)
-- PW is secret123
VALUES ('user', 'fcf730b6d95236ecd3c9fc2d92d7b6b2bb061514961aec041d6c7a7192f592e4', true)
ON CONFLICT (username) DO NOTHING;

INSERT INTO app_user_role (user_id, role)
SELECT id, 'ROLE_USER'
FROM app_user
WHERE username = 'user'
ON CONFLICT DO NOTHING;
