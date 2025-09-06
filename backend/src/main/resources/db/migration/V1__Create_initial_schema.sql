CREATE TABLE IF NOT EXISTS breed
(
    id   BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS behaviour
(
    id   BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cat
(
    id         BIGSERIAL PRIMARY KEY,
    name       TEXT      NOT NULL,
    birth_date DATE,
    breed_id   BIGINT    NOT NULL REFERENCES breed (id) ON DELETE RESTRICT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS cat_behaviour
(
    cat_id       BIGINT NOT NULL REFERENCES cat (id) ON DELETE CASCADE,
    behaviour_id BIGINT NOT NULL REFERENCES behaviour (id) ON DELETE CASCADE,
    PRIMARY KEY (cat_id, behaviour_id)
);

CREATE TABLE IF NOT EXISTS cat_friendship
(
    cat_id    BIGINT NOT NULL REFERENCES cat (id) ON DELETE CASCADE,
    friend_id BIGINT NOT NULL REFERENCES cat (id) ON DELETE CASCADE,
    CONSTRAINT chk_cat_lt_friend CHECK (cat_id < friend_id),
    CONSTRAINT pk_cat_friend PRIMARY KEY (cat_id, friend_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_cat_name ON cat (name);
CREATE INDEX IF NOT EXISTS idx_cat_breed_id ON cat (breed_id);
CREATE INDEX IF NOT EXISTS idx_cat_created_at ON cat (created_at);
CREATE INDEX IF NOT EXISTS idx_cat_birth_date ON cat (birth_date);
CREATE INDEX IF NOT EXISTS idx_cat_breed_id_id ON cat (breed_id, id);
CREATE INDEX IF NOT EXISTS idx_catbeh_behaviour_id ON cat_behaviour (behaviour_id);
CREATE INDEX IF NOT EXISTS idx_catfriend_cat_id ON cat_friendship (cat_id);
CREATE INDEX IF NOT EXISTS idx_catfriend_friend_id ON cat_friendship (friend_id);
