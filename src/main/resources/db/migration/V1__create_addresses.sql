CREATE TABLE addresses
(
    id             BIGSERIAL PRIMARY KEY,
    street         VARCHAR(255) NOT NULL,
    street_number  VARCHAR(20)  NOT NULL,
    city           VARCHAR(100) NOT NULL,
    country        VARCHAR(100) NOT NULL,
    postal_code    VARCHAR(20)  NOT NULL
);
