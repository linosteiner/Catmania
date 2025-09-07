TRUNCATE TABLE cat_friendship, cat_behaviour, cat RESTART IDENTITY CASCADE;

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
ON CONFLICT (name) DO NOTHING;

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
ON CONFLICT (name) DO NOTHING;

WITH seed(name, birth_date, breed_name) AS (VALUES ('Luna', DATE '2020-05-14', 'Siamese'),
                                                   ('Milo', DATE '2019-11-02', 'Maine Coon'),
                                                   ('Cleo', DATE '2021-07-23', 'Persian'),
                                                   ('Simba', DATE '2018-03-12', 'Bengal'),
                                                   ('Nala', DATE '2022-01-01', 'Sphynx'),
                                                   ('Oliver', DATE '2017-09-18', 'British Shorthair'),
                                                   ('Chloe', DATE '2020-02-10', 'Ragdoll'),
                                                   ('Leo', DATE '2016-06-21', 'Russian Blue'),
                                                   ('Misty', DATE '2021-11-05', 'Scottish Fold'),
                                                   ('Tiger', DATE '2019-04-14', 'Norwegian Forest'),
                                                   ('Sasha', DATE '2022-07-30', 'Turkish Angora'),
                                                   ('Charlie', DATE '2020-08-08', 'Abyssinian')),
     resolved AS (SELECT s.name, s.birth_date, b.id AS breed_id
                  FROM seed s
                           JOIN breed b ON b.name = s.breed_name)
INSERT
INTO cat (name, birth_date, breed_id)
SELECT name, birth_date, breed_id
FROM resolved
ON CONFLICT (name) DO UPDATE
    SET birth_date = EXCLUDED.birth_date,
        breed_id   = EXCLUDED.breed_id;

WITH pairs(cat_name, behaviour_name) AS (VALUES ('Luna', 'Friendly'),
                                                ('Luna', 'Playful'),
                                                ('Milo', 'Lazy'),
                                                ('Milo', 'Independent'),
                                                ('Cleo', 'Sassy'),
                                                ('Simba', 'Curious'),
                                                ('Nala', 'Playful'),
                                                ('Oliver', 'Lazy'),
                                                ('Oliver', 'Affectionate'),
                                                ('Chloe', 'Friendly'),
                                                ('Chloe', 'Vocal'),
                                                ('Leo', 'Independent'),
                                                ('Misty', 'Shy'),
                                                ('Tiger', 'Energetic'),
                                                ('Sasha', 'Affectionate'),
                                                ('Charlie', 'Curious')),
     resolved AS (SELECT c.id AS cat_id, b.id AS behaviour_id
                  FROM pairs p
                           JOIN cat c ON c.name = p.cat_name
                           JOIN behaviour b ON b.name = p.behaviour_name)
INSERT
INTO cat_behaviour (cat_id, behaviour_id)
SELECT cat_id, behaviour_id
FROM resolved
ON CONFLICT DO NOTHING;

WITH name_pairs(a_name, b_name) AS (VALUES ('Luna', 'Milo'),
                                           ('Luna', 'Cleo'),
                                           ('Milo', 'Simba'),
                                           ('Cleo', 'Nala'),
                                           ('Oliver', 'Chloe'),
                                           ('Leo', 'Misty'),
                                           ('Tiger', 'Sasha'),
                                           ('Charlie', 'Luna'),
                                           ('Charlie', 'Milo'),
                                           ('Simba', 'Sasha')),
     id_pairs AS (SELECT ca.id AS a, cb.id AS b
                  FROM name_pairs np
                           JOIN cat ca ON ca.name = np.a_name
                           JOIN cat cb ON cb.name = np.b_name),
     normalized AS (SELECT LEAST(a, b) AS cat_id, GREATEST(a, b) AS friend_id FROM id_pairs)
INSERT
INTO cat_friendship (cat_id, friend_id)
SELECT cat_id, friend_id
FROM normalized
ON CONFLICT DO NOTHING;

INSERT INTO app_user (username, password_hash, enabled)
VALUES ('user', 'fcf730b6d95236ecd3c9fc2d92d7b6b2bb061514961aec041d6c7a7192f592e4', true)
ON CONFLICT (username) DO NOTHING;

INSERT INTO app_user_role (user_id, role)
SELECT id, 'ROLE_USER'
FROM app_user
WHERE username = 'user'
ON CONFLICT DO NOTHING;
