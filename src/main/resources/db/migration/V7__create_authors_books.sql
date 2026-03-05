CREATE TABLE authors_books
(
    author_id BIGINT REFERENCES authors(id) NOT NULL,
    book_id   BIGINT REFERENCES books(id)   NOT NULL,
    PRIMARY KEY (author_id, book_id)
);