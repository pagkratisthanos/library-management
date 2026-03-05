CREATE TABLE members
(
    id              BIGSERIAL PRIMARY KEY,
    address_id      BIGINT REFERENCES addresses(id),
    firstname       VARCHAR(100) NOT NULL,
    lastname        VARCHAR(100) NOT NULL,
    phone_number    VARCHAR(20) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    birth_date      DATE,
    membership_date DATE         NOT NULL
);