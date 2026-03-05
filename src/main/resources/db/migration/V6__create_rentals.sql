CREATE TABLE rentals
(
    id          BIGSERIAL PRIMARY KEY,
    member_id   BIGINT    REFERENCES members(id) NOT NULL,
    copy_id     BIGINT    REFERENCES copies(id)  NOT NULL,
    rental_date TIMESTAMP                        NOT NULL,
    due_date    TIMESTAMP                        NOT NULL,
    return_date TIMESTAMP
);