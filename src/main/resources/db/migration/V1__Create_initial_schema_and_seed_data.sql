CREATE TABLE breed
(
    pk   BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE behaviour
(
    pk   BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE cat
(
    pk         BIGSERIAL PRIMARY KEY,
    name       TEXT      NOT NULL,
    birth_date DATE,
    fk_breed   BIGINT    REFERENCES breed (pk) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE cat_behaviour
(
    fk_cat       BIGINT NOT NULL REFERENCES cat (pk) ON DELETE CASCADE,
    fk_behaviour BIGINT NOT NULL REFERENCES behaviour (pk) ON DELETE CASCADE,
    PRIMARY KEY (fk_cat, fk_behaviour)
);

CREATE TABLE cat_friendship
(
    fk_cat    BIGINT NOT NULL REFERENCES cat (pk) ON DELETE CASCADE,
    fk_friend BIGINT NOT NULL REFERENCES cat (pk) ON DELETE CASCADE,
    CHECK ( fk_cat < fk_friend ),
    PRIMARY KEY (fk_cat, fk_friend)
);

INSERT INTO breed (name)
VALUES ('Siamese'),
       ('Persian'),
       ('Maine Coon'),
       ('Bengal'),
       ('Sphynx');

INSERT INTO behaviour (name)
VALUES ('Friendly'),
       ('Sassy'),
       ('Playful'),
       ('Lazy'),
       ('Curious');

INSERT INTO cat (name, birth_date, fk_breed)
VALUES ('Luna', '2020-05-14', (SELECT pk FROM breed WHERE name = 'Siamese')),
       ('Milo', '2019-11-02', (SELECT pk FROM breed WHERE name = 'Maine Coon')),
       ('Cleo', '2021-07-23', (SELECT pk FROM breed WHERE name = 'Persian')),
       ('Simba', '2018-03-12', (SELECT pk FROM breed WHERE name = 'Bengal')),
       ('Nala', '2022-01-01', (SELECT pk FROM breed WHERE name = 'Sphynx'));

INSERT INTO cat_behaviour (fk_cat, fk_behaviour)
VALUES ((SELECT pk FROM cat WHERE name = 'Luna'), (SELECT pk FROM behaviour WHERE name = 'Friendly')),
       ((SELECT pk FROM cat WHERE name = 'Luna'), (SELECT pk FROM behaviour WHERE name = 'Playful')),
       ((SELECT pk FROM cat WHERE name = 'Milo'), (SELECT pk FROM behaviour WHERE name = 'Lazy')),
       ((SELECT pk FROM cat WHERE name = 'Cleo'), (SELECT pk FROM behaviour WHERE name = 'Sassy')),
       ((SELECT pk FROM cat WHERE name = 'Simba'), (SELECT pk FROM behaviour WHERE name = 'Curious')),
       ((SELECT pk FROM cat WHERE name = 'Nala'), (SELECT pk FROM behaviour WHERE name = 'Playful'));

INSERT INTO cat_friendship (fk_cat, fk_friend)
VALUES ((SELECT pk FROM cat WHERE name = 'Luna'), (SELECT pk FROM cat WHERE name = 'Milo')),
       ((SELECT pk FROM cat WHERE name = 'Luna'), (SELECT pk FROM cat WHERE name = 'Cleo')),
       ((SELECT pk FROM cat WHERE name = 'Milo'), (SELECT pk FROM cat WHERE name = 'Simba')),
       ((SELECT pk FROM cat WHERE name = 'Cleo'), (SELECT pk FROM cat WHERE name = 'Nala'));
