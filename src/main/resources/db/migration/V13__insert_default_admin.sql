-- Insert default admin user
-- NOTE: Password hash is incorrect. See V14 for the correct hash.
INSERT INTO users (id, username, password, role_id)
SELECT
    gen_random_uuid(),
    'admin',
    '$2a$12$9UV4tSEPpkPSSmBNSWKMiuMGkROMqBTmLFHzFHJEPHQSPMpFoG4vS',
    r.id
FROM roles r
WHERE r.name = 'ADMIN';