-- Fix incorrect password hash from V13
-- Password: admin123!
UPDATE users
SET password = '$2a$10$ZR4GplkmGSLORA0mnXktm.0EUO8L98aPa273x0kcbwZyAEjR2otSm'
WHERE username = 'admin';