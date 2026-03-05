CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE addresses ADD COLUMN uuid UUID DEFAULT uuid_generate_v4();
ALTER TABLE authors ADD COLUMN uuid UUID DEFAULT uuid_generate_v4();
ALTER TABLE members ADD COLUMN uuid UUID DEFAULT uuid_generate_v4();
ALTER TABLE books ADD COLUMN uuid UUID DEFAULT uuid_generate_v4();
ALTER TABLE copies ADD COLUMN uuid UUID DEFAULT uuid_generate_v4();
ALTER TABLE rentals ADD COLUMN uuid UUID DEFAULT uuid_generate_v4();