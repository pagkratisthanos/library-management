CREATE TABLE roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE capabilities
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE roles_capabilities
(
    role_id       BIGINT NOT NULL REFERENCES roles(id),
    capability_id BIGINT NOT NULL REFERENCES capabilities(id),
    PRIMARY KEY (role_id, capability_id)
);

CREATE TABLE users
(
    id         UUID         PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role_id    BIGINT       NOT NULL REFERENCES roles(id),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ
);

-- Insert roles
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('LIBRARIAN');

-- Insert capabilities
INSERT INTO capabilities (name, description) VALUES ('VIEW_AUTHOR',   'View author details');
INSERT INTO capabilities (name, description) VALUES ('EDIT_AUTHOR',   'Edit author details');
INSERT INTO capabilities (name, description) VALUES ('DELETE_AUTHOR', 'Delete an author');

INSERT INTO capabilities (name, description) VALUES ('VIEW_BOOK',     'View book details');
INSERT INTO capabilities (name, description) VALUES ('EDIT_BOOK',     'Edit book details');
INSERT INTO capabilities (name, description) VALUES ('DELETE_BOOK',   'Delete a book');

INSERT INTO capabilities (name, description) VALUES ('VIEW_MEMBER',   'View member details');
INSERT INTO capabilities (name, description) VALUES ('EDIT_MEMBER',   'Edit member details');
INSERT INTO capabilities (name, description) VALUES ('DELETE_MEMBER', 'Delete a member');

INSERT INTO capabilities (name, description) VALUES ('VIEW_COPY',     'View copy details');
INSERT INTO capabilities (name, description) VALUES ('EDIT_COPY',     'Edit copy details');
INSERT INTO capabilities (name, description) VALUES ('DELETE_COPY',   'Delete a copy');

INSERT INTO capabilities (name, description) VALUES ('VIEW_RENTAL',   'View rental details');
INSERT INTO capabilities (name, description) VALUES ('MANAGE_RENTAL', 'Create and return rentals');

INSERT INTO capabilities (name, description) VALUES ('MANAGE_USERS',  'Create and delete users');

-- Assign ALL capabilities to ADMIN
INSERT INTO roles_capabilities (role_id, capability_id)
SELECT r.id, c.id
FROM roles r
JOIN capabilities c ON TRUE
WHERE r.name = 'ADMIN';

-- Assign LIMITED capabilities to LIBRARIAN
INSERT INTO roles_capabilities (role_id, capability_id)
SELECT r.id, c.id
FROM roles r
JOIN capabilities c ON TRUE
WHERE r.name = 'LIBRARIAN'
AND c.name IN (
    'VIEW_AUTHOR',
    'VIEW_BOOK',
    'VIEW_MEMBER',   'EDIT_MEMBER',   'DELETE_MEMBER',
    'VIEW_COPY',     'EDIT_COPY',     'DELETE_COPY',
    'VIEW_RENTAL',   'MANAGE_RENTAL'
);