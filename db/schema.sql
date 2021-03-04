CREATE TABLE post
(
    id   SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE candidate
(
    id       SERIAL PRIMARY KEY,
    name     TEXT,
    photo_id INTEGER
);

CREATE TABLE photo
(
    id   SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    name     TEXT,
    email    TEXT,
    password TEXT
);

CREATE TABLE cities
(
    id   SERIAL PRIMARY KEY,
    name TEXT
);


INSERT INTO cities (city)
VALUES ('Москва'),
       ('Санкт-Петербург'),
       ('Новосибирск'),
       ('Екатеринбург'),
       ('Казань'),
       ('Челябинск'),
       ('Самара'),
       ('Омск'),
       ('Ростов-на-Дону'),
       ('Уфа'),
       ('Красноярск'),
       ('Воронеж');

ALTER TABLE candidate ADD city_id bigint;