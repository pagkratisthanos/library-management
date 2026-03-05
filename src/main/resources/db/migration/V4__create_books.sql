CREATE TABLE books
(
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    isbn           VARCHAR(20)  NOT NULL UNIQUE,
    published_date DATE,
    language       VARCHAR(50),
    daily_cost     DECIMAL(10,2) NOT NULL,
    description    TEXT
);