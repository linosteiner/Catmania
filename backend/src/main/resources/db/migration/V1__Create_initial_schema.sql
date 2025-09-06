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

INSERT INTO breed (name)
VALUES ('Siamese'),
       ('Persian'),
       ('Maine Coon'),
       ('Bengal'),
       ('Sphynx'),
       ('Ragdoll'),
       ('British Shorthair'),
       ('Abyssinian'),
       ('Russian Blue'),
       ('Scottish Fold'),
       ('Norwegian Forest'),
       ('Turkish Angora')
ON CONFLICT DO NOTHING;

INSERT INTO behaviour (name)
VALUES ('Friendly'),
       ('Sassy'),
       ('Playful'),
       ('Lazy'),
       ('Curious'),
       ('Independent'),
       ('Affectionate'),
       ('Vocal'),
       ('Shy'),
       ('Energetic')
ON CONFLICT DO NOTHING;

INSERT INTO cat (name, birth_date, breed_id)
VALUES ('Luna', '2020-05-14', (SELECT id FROM breed WHERE name = 'Siamese')),
       ('Milo', '2019-11-02', (SELECT id FROM breed WHERE name = 'Maine Coon')),
       ('Cleo', '2021-07-23', (SELECT id FROM breed WHERE name = 'Persian')),
       ('Simba', '2018-03-12', (SELECT id FROM breed WHERE name = 'Bengal')),
       ('Nala', '2022-01-01', (SELECT id FROM breed WHERE name = 'Sphynx')),
       ('Oliver', '2017-09-18', (SELECT id FROM breed WHERE name = 'British Shorthair')),
       ('Chloe', '2020-02-10', (SELECT id FROM breed WHERE name = 'Ragdoll')),
       ('Leo', '2016-06-21', (SELECT id FROM breed WHERE name = 'Russian Blue')),
       ('Misty', '2021-11-05', (SELECT id FROM breed WHERE name = 'Scottish Fold')),
       ('Tiger', '2019-04-14', (SELECT id FROM breed WHERE name = 'Norwegian Forest')),
       ('Sasha', '2022-07-30', (SELECT id FROM breed WHERE name = 'Turkish Angora')),
       ('Charlie', '2020-08-08', (SELECT id FROM breed WHERE name = 'Abyssinian'))
ON CONFLICT DO NOTHING;

INSERT INTO cat_behaviour (cat_id, behaviour_id)
VALUES ((SELECT id FROM cat WHERE name = 'Luna'), (SELECT id FROM behaviour WHERE name = 'Friendly')),
       ((SELECT id FROM cat WHERE name = 'Luna'), (SELECT id FROM behaviour WHERE name = 'Playful')),
       ((SELECT id FROM cat WHERE name = 'Milo'), (SELECT id FROM behaviour WHERE name = 'Lazy')),
       ((SELECT id FROM cat WHERE name = 'Milo'), (SELECT id FROM behaviour WHERE name = 'Independent')),
       ((SELECT id FROM cat WHERE name = 'Cleo'), (SELECT id FROM behaviour WHERE name = 'Sassy')),
       ((SELECT id FROM cat WHERE name = 'Simba'), (SELECT id FROM behaviour WHERE name = 'Curious')),
       ((SELECT id FROM cat WHERE name = 'Nala'), (SELECT id FROM behaviour WHERE name = 'Playful')),
       ((SELECT id FROM cat WHERE name = 'Oliver'), (SELECT id FROM behaviour WHERE name = 'Lazy')),
       ((SELECT id FROM cat WHERE name = 'Oliver'), (SELECT id FROM behaviour WHERE name = 'Affectionate')),
       ((SELECT id FROM cat WHERE name = 'Chloe'), (SELECT id FROM behaviour WHERE name = 'Friendly')),
       ((SELECT id FROM cat WHERE name = 'Chloe'), (SELECT id FROM behaviour WHERE name = 'Vocal')),
       ((SELECT id FROM cat WHERE name = 'Leo'), (SELECT id FROM behaviour WHERE name = 'Independent')),
       ((SELECT id FROM cat WHERE name = 'Misty'), (SELECT id FROM behaviour WHERE name = 'Shy')),
       ((SELECT id FROM cat WHERE name = 'Tiger'), (SELECT id FROM behaviour WHERE name = 'Energetic')),
       ((SELECT id FROM cat WHERE name = 'Sasha'), (SELECT id FROM behaviour WHERE name = 'Affectionate')),
       ((SELECT id FROM cat WHERE name = 'Charlie'), (SELECT id FROM behaviour WHERE name = 'Curious'))
ON CONFLICT DO NOTHING;

INSERT INTO cat_friendship (cat_id, friend_id)
VALUES (LEAST((SELECT id FROM cat WHERE name = 'Luna'), (SELECT id FROM cat WHERE name = 'Milo')),
        GREATEST((SELECT id FROM cat WHERE name = 'Luna'), (SELECT id FROM cat WHERE name = 'Milo'))),

       (LEAST((SELECT id FROM cat WHERE name = 'Luna'), (SELECT id FROM cat WHERE name = 'Cleo')),
        GREATEST((SELECT id FROM cat WHERE name = 'Luna'), (SELECT id FROM cat WHERE name = 'Cleo'))),

       (LEAST((SELECT id FROM cat WHERE name = 'Milo'), (SELECT id FROM cat WHERE name = 'Simba')),
        GREATEST((SELECT id FROM cat WHERE name = 'Milo'), (SELECT id FROM cat WHERE name = 'Simba'))),

       (LEAST((SELECT id FROM cat WHERE name = 'Cleo'), (SELECT id FROM cat WHERE name = 'Nala')),
        GREATEST((SELECT id FROM cat WHERE name = 'Cleo'), (SELECT id FROM cat WHERE name = 'Nala'))),

       (LEAST((SELECT id FROM cat WHERE name = 'Oliver'), (SELECT id FROM cat WHERE name = 'Chloe')),
        GREATEST((SELECT id FROM cat WHERE name = 'Oliver'), (SELECT id FROM cat WHERE name = 'Chloe'))),

       (LEAST((SELECT id FROM cat WHERE name = 'Leo'), (SELECT id FROM cat WHERE name = 'Misty')),
        GREATEST((SELECT id FROM cat WHERE name = 'Leo'), (SELECT id FROM cat WHERE name = 'Misty'))),

       (LEAST((SELECT id FROM cat WHERE name = 'Tiger'), (SELECT id FROM cat WHERE name = 'Sasha')),
        GREATEST((SELECT id FROM cat WHERE name = 'Tiger'), (SELECT id FROM cat WHERE name = 'Sasha'))),

       (LEAST((SELECT id FROM cat WHERE name = 'Charlie'), (SELECT id FROM cat WHERE name = 'Luna')),
        GREATEST((SELECT id FROM cat WHERE name = 'Charlie'), (SELECT id FROM cat WHERE name = 'Luna'))),

       (LEAST((SELECT id FROM cat WHERE name = 'Charlie'), (SELECT id FROM cat WHERE name = 'Milo')),
        GREATEST((SELECT id FROM cat WHERE name = 'Charlie'), (SELECT id FROM cat WHERE name = 'Milo'))),

       (LEAST((SELECT id FROM cat WHERE name = 'Simba'), (SELECT id FROM cat WHERE name = 'Sasha')),
        GREATEST((SELECT id FROM cat WHERE name = 'Simba'), (SELECT id FROM cat WHERE name = 'Sasha')))
ON CONFLICT DO NOTHING;

CREATE INDEX IF NOT EXISTS idx_cat_breed_id ON cat (breed_id);
CREATE INDEX IF NOT EXISTS  idx_catbeh_behaviour_id ON cat_behaviour (behaviour_id);
CREATE INDEX IF NOT EXISTS  idx_catfriend_cat_id ON cat_friendship (cat_id);
CREATE INDEX IF NOT EXISTS  idx_catfriend_friend_id ON cat_friendship (friend_id);
CREATE INDEX IF NOT EXISTS  idx_cat_created_at ON cat (created_at);
CREATE INDEX IF NOT EXISTS  idx_cat_birth_date ON cat (birth_date);
CREATE INDEX IF NOT EXISTS  idx_cat_breed_id_id ON cat (breed_id, id);
