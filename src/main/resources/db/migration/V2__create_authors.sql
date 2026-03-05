CREATE TABLE authors
(
    id          BIGSERIAL PRIMARY KEY,
    firstname   VARCHAR(100) NOT NULL,
    lastname    VARCHAR(100) NOT NULL,
    birth_date  DATE,
    birth_place VARCHAR(100),
    bio         TEXT
);