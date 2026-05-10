ALTER TABLE members DROP CONSTRAINT members_address_id_fkey;
ALTER TABLE copies DROP CONSTRAINT copies_book_id_fkey;
ALTER TABLE rentals DROP CONSTRAINT rentals_member_id_fkey;
ALTER TABLE rentals DROP CONSTRAINT rentals_copy_id_fkey;
ALTER TABLE authors_books DROP CONSTRAINT authors_books_author_id_fkey;
ALTER TABLE authors_books DROP CONSTRAINT authors_books_book_id_fkey;

ALTER TABLE addresses DROP CONSTRAINT addresses_pkey;
ALTER TABLE addresses DROP COLUMN id;
ALTER TABLE addresses RENAME COLUMN uuid TO id;
ALTER TABLE addresses ADD PRIMARY KEY (id);
ALTER TABLE addresses ALTER COLUMN id SET NOT NULL;

ALTER TABLE authors DROP CONSTRAINT authors_pkey;
ALTER TABLE authors DROP COLUMN id;
ALTER TABLE authors RENAME COLUMN uuid TO id;
ALTER TABLE authors ADD PRIMARY KEY (id);
ALTER TABLE authors ALTER COLUMN id SET NOT NULL;

ALTER TABLE members DROP CONSTRAINT members_pkey;
ALTER TABLE members DROP COLUMN id;
ALTER TABLE members RENAME COLUMN uuid TO id;
ALTER TABLE members ADD PRIMARY KEY (id);
ALTER TABLE members ALTER COLUMN id SET NOT NULL;

ALTER TABLE books DROP CONSTRAINT books_pkey;
ALTER TABLE books DROP COLUMN id;
ALTER TABLE books RENAME COLUMN uuid TO id;
ALTER TABLE books ADD PRIMARY KEY (id);
ALTER TABLE books ALTER COLUMN id SET NOT NULL;

ALTER TABLE copies DROP CONSTRAINT copies_pkey;
ALTER TABLE copies DROP COLUMN id;
ALTER TABLE copies RENAME COLUMN uuid TO id;
ALTER TABLE copies ADD PRIMARY KEY (id);
ALTER TABLE copies ALTER COLUMN id SET NOT NULL;

ALTER TABLE rentals DROP CONSTRAINT rentals_pkey;
ALTER TABLE rentals DROP COLUMN id;
ALTER TABLE rentals RENAME COLUMN uuid TO id;
ALTER TABLE rentals ADD PRIMARY KEY (id);
ALTER TABLE rentals ALTER COLUMN id SET NOT NULL;

ALTER TABLE members ALTER COLUMN address_id TYPE UUID USING address_id::text::uuid;
ALTER TABLE copies ALTER COLUMN book_id TYPE UUID USING book_id::text::uuid;
ALTER TABLE rentals ALTER COLUMN member_id TYPE UUID USING member_id::text::uuid;
ALTER TABLE rentals ALTER COLUMN copy_id TYPE UUID USING copy_id::text::uuid;
ALTER TABLE authors_books ALTER COLUMN author_id TYPE UUID USING author_id::text::uuid;
ALTER TABLE authors_books ALTER COLUMN book_id TYPE UUID USING book_id::text::uuid;

ALTER TABLE members ADD CONSTRAINT members_address_id_fkey
    FOREIGN KEY (address_id) REFERENCES addresses(id);
ALTER TABLE copies ADD CONSTRAINT copies_book_id_fkey
    FOREIGN KEY (book_id) REFERENCES books(id);
ALTER TABLE rentals ADD CONSTRAINT rentals_member_id_fkey
    FOREIGN KEY (member_id) REFERENCES members(id);
ALTER TABLE rentals ADD CONSTRAINT rentals_copy_id_fkey
    FOREIGN KEY (copy_id) REFERENCES copies(id);
ALTER TABLE authors_books ADD CONSTRAINT authors_books_author_id_fkey
    FOREIGN KEY (author_id) REFERENCES authors(id);
ALTER TABLE authors_books ADD CONSTRAINT authors_books_book_id_fkey
    FOREIGN KEY (book_id) REFERENCES books(id);