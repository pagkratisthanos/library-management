CREATE TABLE copies
(
    id        BIGSERIAL PRIMARY KEY,
    book_id   BIGINT REFERENCES books(id) NOT NULL,
    available BOOLEAN                     NOT NULL DEFAULT TRUE,
    condition VARCHAR(20)                 NOT NULL
);
