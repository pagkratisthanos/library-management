-- Demo data, so that a fresh installation is explorable instead of empty.
--
-- The identifiers are written out rather than generated. That is deliberate: rows have
-- to reference each other, and gen_random_uuid() gives back a value the next statement
-- cannot see. Books and members could be looked up by ISBN or email, but copies have no
-- natural key — three copies of one title differ only by id — and rentals point at a
-- specific copy. Fixed identifiers are the usual answer for seed data; the values are
-- still valid, unique UUIDs.
--
-- Delete this migration if you want to start from an empty library.

-- ---------------------------------------------------------------- authors
-- The last five have no books.
INSERT INTO authors (id, firstname, lastname, birth_date, birth_place, bio) VALUES
('0a000000-0000-4000-8000-000000000001', 'George',   'Orwell',         '1903-06-25', 'Motihari, India',        'English novelist and essayist, known for political allegory and the plainest prose of his generation.'),
('0a000000-0000-4000-8000-000000000002', 'Jane',     'Austen',         '1775-12-16', 'Steventon, England',     'English novelist whose comedies of manners hide a very sharp knife.'),
('0a000000-0000-4000-8000-000000000003', 'Haruki',   'Murakami',       '1949-01-12', 'Kyoto, Japan',           'Japanese novelist who lets the surreal walk into ordinary rooms.'),
('0a000000-0000-4000-8000-000000000004', 'Umberto',  'Eco',            '1932-01-05', 'Alessandria, Italy',     'Italian medievalist and semiotician who wrote bestsellers by accident.'),
('0a000000-0000-4000-8000-000000000005', 'Mikhail',  'Bulgakov',       '1891-05-15', 'Kyiv, Ukraine',          'Russian writer and playwright who kept writing a banned novel for twelve years.'),
('0a000000-0000-4000-8000-000000000006', 'Gabriel',  'Garcia Marquez', '1927-03-06', 'Aracataca, Colombia',    'Colombian novelist and the central figure of magical realism.'),
('0a000000-0000-4000-8000-000000000007', 'Albert',   'Camus',          '1913-11-07', 'Drean, Algeria',         'French-Algerian novelist and philosopher of the absurd.'),
('0a000000-0000-4000-8000-000000000008', 'Fyodor',   'Dostoevsky',     '1821-11-11', 'Moscow, Russia',         'Russian novelist who put the argument before the plot and won anyway.'),
('0a000000-0000-4000-8000-000000000009', 'Virginia', 'Woolf',          '1882-01-25', 'London, England',        'English modernist, a pioneer of stream of consciousness narration.'),
('0a000000-0000-4000-8000-00000000000a', 'Kazuo',    'Ishiguro',       '1954-11-08', 'Nagasaki, Japan',        'British novelist whose narrators are always the last to understand themselves.'),
('0a000000-0000-4000-8000-00000000000b', 'Margaret', 'Atwood',         '1939-11-18', 'Ottawa, Canada',         'Canadian poet and novelist, known for speculative fiction with a documentary tone.'),
('0a000000-0000-4000-8000-00000000000c', 'Italo',    'Calvino',        '1923-10-15', 'Santiago de las Vegas, Cuba', 'Italian writer of fables, cities and books about books.'),
('0a000000-0000-4000-8000-00000000000d', 'Nikos',    'Kazantzakis',    '1883-02-18', 'Heraklion, Greece',      'Greek novelist and philosopher, the most translated Greek writer of the century.'),
('0a000000-0000-4000-8000-00000000000e', 'Franz',    'Kafka',          '1883-07-03', 'Prague, Bohemia',        'Czech writer in German whose surname became an adjective.'),
('0a000000-0000-4000-8000-00000000000f', 'Toni',     'Morrison',       '1931-02-18', 'Lorain, United States',  'American novelist and Nobel laureate who wrote memory as a physical presence.'),
('0a000000-0000-4000-8000-000000000010', 'Jorge Luis', 'Borges',       '1899-08-24', 'Buenos Aires, Argentina','Argentine writer of labyrinths, libraries and forged reviews of imaginary books.'),
('0a000000-0000-4000-8000-000000000011', 'Chinua',   'Achebe',         '1930-11-16', 'Ogidi, Nigeria',         'Nigerian novelist, the founding figure of modern African literature in English.'),
('0a000000-0000-4000-8000-000000000012', 'Milan',    'Kundera',        '1929-04-01', 'Brno, Czechoslovakia',   'Czech-French novelist and essayist on lightness, memory and forgetting.'),
('0a000000-0000-4000-8000-000000000013', 'Jose',     'Saramago',       '1922-11-16', 'Azinhaga, Portugal',     'Portuguese novelist and Nobel laureate who wrote without quotation marks.'),
('0a000000-0000-4000-8000-000000000014', 'Doris',    'Lessing',        '1919-10-22', 'Kermanshah, Persia',     'British novelist and Nobel laureate, from realism to science fiction and back.'),
('0a000000-0000-4000-8000-000000000015', 'Isabel',   'Allende',        '1942-08-02', 'Lima, Peru',             'Chilean novelist whose family chronicles carry a country''s history.'),
('0a000000-0000-4000-8000-000000000016', 'Orhan',    'Pamuk',          '1952-06-07', 'Istanbul, Turkey',       'Turkish novelist and Nobel laureate, chronicler of Istanbul''s melancholy.'),
('0a000000-0000-4000-8000-000000000017', 'Elena',    'Ferrante',       NULL,         NULL,                     'Pseudonymous Italian novelist. Her identity, and her date of birth, are unknown.'),
('0a000000-0000-4000-8000-000000000018', 'Yannis',   'Ritsos',         '1909-05-01', 'Monemvasia, Greece',     'Greek poet, one of the great voices of twentieth century Greek verse.');

-- ------------------------------------------------------------------ books
INSERT INTO books (id, title, isbn, published_date, language, daily_cost, description) VALUES
('0b000000-0000-4000-8000-000000000001', 'Animal Farm',                       '978-0452284241', '1945-08-17', 'English',    1.20, 'Farm animals overthrow their farmer and build a society that curdles into the thing it replaced.'),
('0b000000-0000-4000-8000-000000000002', 'Nineteen Eighty-Four',              '978-0451524935', '1949-06-08', 'English',    1.60, 'A clerk in a total surveillance state tries to keep one private thought.'),
('0b000000-0000-4000-8000-000000000003', 'Pride and Prejudice',               '978-0141439518', '1813-01-28', 'English',    1.40, 'Elizabeth Bennet and Mr Darcy misread each other for four hundred pages.'),
('0b000000-0000-4000-8000-000000000004', 'Emma',                              '978-0141439587', '1815-12-23', 'English',    1.30, 'A young woman with too much free time arranges other people''s marriages, badly.'),
('0b000000-0000-4000-8000-000000000005', 'Kafka on the Shore',                '978-1400079278', '2002-09-12', 'Japanese',   2.00, 'A runaway boy and an old man who talks to cats move toward the same point from opposite ends of Japan.'),
('0b000000-0000-4000-8000-000000000006', 'Norwegian Wood',                    '978-0375704024', '1987-09-04', 'Japanese',   1.80, 'A student in 1960s Tokyo is caught between the girl he lost and the one in front of him.'),
('0b000000-0000-4000-8000-000000000007', 'The Name of the Rose',              '978-0156001311', '1980-01-01', 'Italian',    2.20, 'A Franciscan friar investigates a series of deaths in a medieval abbey with a very well defended library.'),
('0b000000-0000-4000-8000-000000000008', 'Foucault''s Pendulum',              '978-0156032971', '1988-01-01', 'Italian',    2.30, 'Three editors invent a conspiracy as a joke and are believed by people who do not joke.'),
('0b000000-0000-4000-8000-000000000009', 'The Master and Margarita',          '978-0143108276', '1967-01-01', 'Russian',    2.50, 'The devil visits Soviet Moscow, and the bureaucracy is the one that comes off worse.'),
('0b000000-0000-4000-8000-00000000000a', 'One Hundred Years of Solitude',     '978-0060883287', '1967-05-30', 'Spanish',    2.30, 'Seven generations of the Buendia family live, and repeat, their history in Macondo.'),
('0b000000-0000-4000-8000-00000000000b', 'Love in the Time of Cholera',       '978-0307389732', '1985-01-01', 'Spanish',    2.10, 'A man waits fifty-one years, nine months and four days for a second answer.'),
('0b000000-0000-4000-8000-00000000000c', 'The Stranger',                      '978-0679720201', '1942-01-01', 'French',     1.80, 'A man is tried less for the murder he committed than for the grief he failed to show.'),
('0b000000-0000-4000-8000-00000000000d', 'The Plague',                        '978-0679720218', '1947-01-01', 'French',     1.90, 'A city is sealed off, and its inhabitants discover what they are actually made of.'),
('0b000000-0000-4000-8000-00000000000e', 'Crime and Punishment',              '978-0140449136', '1866-01-01', 'Russian',    2.40, 'A destitute student murders a pawnbroker and unravels under his own conscience.'),
('0b000000-0000-4000-8000-00000000000f', 'The Brothers Karamazov',            '978-0374528379', '1880-11-01', 'Russian',    2.70, 'Four brothers, one murdered father, and every argument about God that matters.'),
('0b000000-0000-4000-8000-000000000010', 'Mrs Dalloway',                      '978-0156628709', '1925-05-14', 'English',    1.50, 'One day in London, told from inside the head of everyone who passes through it.'),
('0b000000-0000-4000-8000-000000000011', 'The Remains of the Day',            '978-0679731726', '1989-05-01', 'English',    1.70, 'An English butler recounts a life of service and never quite says what it cost him.'),
('0b000000-0000-4000-8000-000000000012', 'Never Let Me Go',                   '978-1400078776', '2005-03-03', 'English',    1.90, 'Three friends at an English boarding school slowly learn what they were made for.'),
('0b000000-0000-4000-8000-000000000013', 'The Handmaid''s Tale',              '978-0385490818', '1985-08-01', 'English',    1.90, 'A theocracy reduces women to their fertility, narrated by one of them.'),
('0b000000-0000-4000-8000-000000000014', 'If on a Winter''s Night a Traveler','978-0156439619', '1979-01-01', 'Italian',    2.10, 'A novel about reading a novel that keeps being interrupted by other novels.'),
('0b000000-0000-4000-8000-000000000015', 'Zorba the Greek',                   '978-0684825540', '1946-01-01', 'Greek',      1.60, 'A bookish narrator meets a man who has read nothing and understood everything.'),
('0b000000-0000-4000-8000-000000000016', 'The Trial',                         '978-0805209990', '1925-04-26', 'German',     1.70, 'A man is arrested one morning and never learns the charge.'),
('0b000000-0000-4000-8000-000000000017', 'Beloved',                           '978-1400033416', '1987-09-02', 'English',    2.00, 'A house outside Cincinnati is haunted by a daughter and by what slavery made her mother do.'),
('0b000000-0000-4000-8000-000000000018', 'Ficciones',                         '978-0802130303', '1944-01-01', 'Spanish',    1.95, 'Short stories about infinite libraries, forking gardens and books that read themselves.'),
('0b000000-0000-4000-8000-000000000019', 'Things Fall Apart',                 '978-0385474542', '1958-06-17', 'English',    1.75, 'A leader in an Igbo village holds his world together until the missionaries arrive.'),
('0b000000-0000-4000-8000-00000000001a', 'Voices from Europe: An Anthology',  '978-0000000001', '1998-04-01', 'English',    2.60, 'Three novelists on borders, memory and the century they had just finished living through.');

-- --------------------------------------------------------- authors ↔ books
INSERT INTO authors_books (author_id, book_id) VALUES
('0a000000-0000-4000-8000-000000000001', '0b000000-0000-4000-8000-000000000001'),
('0a000000-0000-4000-8000-000000000001', '0b000000-0000-4000-8000-000000000002'),
('0a000000-0000-4000-8000-000000000002', '0b000000-0000-4000-8000-000000000003'),
('0a000000-0000-4000-8000-000000000002', '0b000000-0000-4000-8000-000000000004'),
('0a000000-0000-4000-8000-000000000003', '0b000000-0000-4000-8000-000000000005'),
('0a000000-0000-4000-8000-000000000003', '0b000000-0000-4000-8000-000000000006'),
('0a000000-0000-4000-8000-000000000004', '0b000000-0000-4000-8000-000000000007'),
('0a000000-0000-4000-8000-000000000004', '0b000000-0000-4000-8000-000000000008'),
('0a000000-0000-4000-8000-000000000005', '0b000000-0000-4000-8000-000000000009'),
('0a000000-0000-4000-8000-000000000006', '0b000000-0000-4000-8000-00000000000a'),
('0a000000-0000-4000-8000-000000000006', '0b000000-0000-4000-8000-00000000000b'),
('0a000000-0000-4000-8000-000000000007', '0b000000-0000-4000-8000-00000000000c'),
('0a000000-0000-4000-8000-000000000007', '0b000000-0000-4000-8000-00000000000d'),
('0a000000-0000-4000-8000-000000000008', '0b000000-0000-4000-8000-00000000000e'),
('0a000000-0000-4000-8000-000000000008', '0b000000-0000-4000-8000-00000000000f'),
('0a000000-0000-4000-8000-000000000009', '0b000000-0000-4000-8000-000000000010'),
('0a000000-0000-4000-8000-00000000000a', '0b000000-0000-4000-8000-000000000011'),
('0a000000-0000-4000-8000-00000000000a', '0b000000-0000-4000-8000-000000000012'),
('0a000000-0000-4000-8000-00000000000b', '0b000000-0000-4000-8000-000000000013'),
('0a000000-0000-4000-8000-00000000000c', '0b000000-0000-4000-8000-000000000014'),
('0a000000-0000-4000-8000-00000000000d', '0b000000-0000-4000-8000-000000000015'),
('0a000000-0000-4000-8000-00000000000e', '0b000000-0000-4000-8000-000000000016'),
('0a000000-0000-4000-8000-00000000000f', '0b000000-0000-4000-8000-000000000017'),
('0a000000-0000-4000-8000-000000000010', '0b000000-0000-4000-8000-000000000018'),
('0a000000-0000-4000-8000-000000000011', '0b000000-0000-4000-8000-000000000019'),
-- the anthology has three authors
('0a000000-0000-4000-8000-00000000000c', '0b000000-0000-4000-8000-00000000001a'),
('0a000000-0000-4000-8000-000000000012', '0b000000-0000-4000-8000-00000000001a'),
('0a000000-0000-4000-8000-000000000013', '0b000000-0000-4000-8000-00000000001a');

-- ----------------------------------------------------------------- copies
-- available = FALSE means the copy is out on loan right now
INSERT INTO copies (id, book_id, available, condition) VALUES
('0c000000-0000-4000-8000-000000000001', '0b000000-0000-4000-8000-000000000001', FALSE, 'GOOD'),
('0c000000-0000-4000-8000-000000000002', '0b000000-0000-4000-8000-000000000001', TRUE,  'NEW'),
('0c000000-0000-4000-8000-000000000003', '0b000000-0000-4000-8000-000000000001', TRUE,  'FAIR'),
('0c000000-0000-4000-8000-000000000004', '0b000000-0000-4000-8000-000000000002', TRUE,  'NEW'),
('0c000000-0000-4000-8000-000000000005', '0b000000-0000-4000-8000-000000000002', TRUE,  'POOR'),
('0c000000-0000-4000-8000-000000000006', '0b000000-0000-4000-8000-000000000003', TRUE,  'GOOD'),
('0c000000-0000-4000-8000-000000000007', '0b000000-0000-4000-8000-000000000003', TRUE,  'DAMAGED'),
('0c000000-0000-4000-8000-000000000008', '0b000000-0000-4000-8000-000000000004', TRUE,  'GOOD'),
-- Kafka on the Shore: one copy out and overdue
('0c000000-0000-4000-8000-000000000009', '0b000000-0000-4000-8000-000000000005', FALSE, 'NEW'),
('0c000000-0000-4000-8000-00000000000a', '0b000000-0000-4000-8000-000000000005', TRUE,  'GOOD'),
('0c000000-0000-4000-8000-00000000000b', '0b000000-0000-4000-8000-000000000006', TRUE,  'FAIR'),
('0c000000-0000-4000-8000-00000000000c', '0b000000-0000-4000-8000-000000000006', TRUE,  'GOOD'),
-- The Name of the Rose: every copy is out, so the catalogue shows 0 of 3
('0c000000-0000-4000-8000-00000000000d', '0b000000-0000-4000-8000-000000000007', FALSE, 'NEW'),
('0c000000-0000-4000-8000-00000000000e', '0b000000-0000-4000-8000-000000000007', FALSE, 'GOOD'),
('0c000000-0000-4000-8000-00000000000f', '0b000000-0000-4000-8000-000000000007', FALSE, 'FAIR'),
('0c000000-0000-4000-8000-000000000010', '0b000000-0000-4000-8000-000000000008', TRUE,  'GOOD'),
('0c000000-0000-4000-8000-000000000011', '0b000000-0000-4000-8000-000000000009', TRUE,  'NEW'),
('0c000000-0000-4000-8000-000000000012', '0b000000-0000-4000-8000-000000000009', TRUE,  'GOOD'),
('0c000000-0000-4000-8000-000000000013', '0b000000-0000-4000-8000-00000000000a', TRUE,  'GOOD'),
('0c000000-0000-4000-8000-000000000014', '0b000000-0000-4000-8000-00000000000a', TRUE,  'POOR'),
('0c000000-0000-4000-8000-000000000015', '0b000000-0000-4000-8000-00000000000b', TRUE,  'FAIR'),
('0c000000-0000-4000-8000-000000000016', '0b000000-0000-4000-8000-00000000000c', TRUE,  'GOOD'),
('0c000000-0000-4000-8000-000000000017', '0b000000-0000-4000-8000-00000000000c', TRUE,  'NEW'),
('0c000000-0000-4000-8000-000000000018', '0b000000-0000-4000-8000-00000000000d', TRUE,  'DAMAGED'),
-- Crime and Punishment: one copy out
('0c000000-0000-4000-8000-000000000019', '0b000000-0000-4000-8000-00000000000e', FALSE, 'NEW'),
('0c000000-0000-4000-8000-00000000001a', '0b000000-0000-4000-8000-00000000000e', TRUE,  'DAMAGED'),
('0c000000-0000-4000-8000-00000000001b', '0b000000-0000-4000-8000-00000000000f', TRUE,  'GOOD'),
('0c000000-0000-4000-8000-00000000001c', '0b000000-0000-4000-8000-00000000000f', TRUE,  'POOR'),
('0c000000-0000-4000-8000-00000000001d', '0b000000-0000-4000-8000-000000000010', TRUE,  'FAIR'),
('0c000000-0000-4000-8000-00000000001e', '0b000000-0000-4000-8000-000000000011', TRUE,  'NEW'),
('0c000000-0000-4000-8000-00000000001f', '0b000000-0000-4000-8000-000000000011', TRUE,  'GOOD'),
('0c000000-0000-4000-8000-000000000020', '0b000000-0000-4000-8000-000000000012', TRUE,  'GOOD'),
('0c000000-0000-4000-8000-000000000021', '0b000000-0000-4000-8000-000000000012', TRUE,  'FAIR'),
-- The Handmaid's Tale has no copies at all
('0c000000-0000-4000-8000-000000000022', '0b000000-0000-4000-8000-000000000014', TRUE,  'NEW'),
-- Zorba the Greek: one copy out and overdue
('0c000000-0000-4000-8000-000000000023', '0b000000-0000-4000-8000-000000000015', FALSE, 'GOOD'),
('0c000000-0000-4000-8000-000000000024', '0b000000-0000-4000-8000-000000000015', TRUE,  'FAIR'),
('0c000000-0000-4000-8000-000000000025', '0b000000-0000-4000-8000-000000000015', TRUE,  'POOR'),
('0c000000-0000-4000-8000-000000000026', '0b000000-0000-4000-8000-000000000016', TRUE,  'NEW'),
('0c000000-0000-4000-8000-000000000027', '0b000000-0000-4000-8000-000000000016', TRUE,  'DAMAGED'),
('0c000000-0000-4000-8000-000000000028', '0b000000-0000-4000-8000-000000000017', TRUE,  'GOOD'),
('0c000000-0000-4000-8000-000000000029', '0b000000-0000-4000-8000-000000000017', TRUE,  'NEW'),
('0c000000-0000-4000-8000-00000000002a', '0b000000-0000-4000-8000-000000000018', TRUE,  'FAIR'),
-- Things Fall Apart has no copies either
-- the anthology: one copy out and overdue
('0c000000-0000-4000-8000-00000000002b', '0b000000-0000-4000-8000-00000000001a', FALSE, 'NEW'),
('0c000000-0000-4000-8000-00000000002c', '0b000000-0000-4000-8000-00000000001a', TRUE,  'GOOD'),
('0c000000-0000-4000-8000-00000000002d', '0b000000-0000-4000-8000-00000000001a', TRUE,  'POOR');

-- -------------------------------------------------------------- addresses
INSERT INTO addresses (id, street, street_number, city, country, postal_code) VALUES
('0e000000-0000-4000-8000-000000000001', 'Ermou',           '15',  'Athens',       'Greece', '10563'),
('0e000000-0000-4000-8000-000000000002', 'Tsimiski',        '42',  'Thessaloniki', 'Greece', '54623'),
('0e000000-0000-4000-8000-000000000003', 'Korinthou',       '8',   'Patras',       'Greece', '26221'),
('0e000000-0000-4000-8000-000000000004', 'Alfeiou',         '4',   'Athens',       'Greece', '11523'),
('0e000000-0000-4000-8000-000000000005', 'Mavromichali',    '126', 'Athens',       'Greece', '11472'),
('0e000000-0000-4000-8000-000000000006', 'Patision',        '76',  'Athens',       'Greece', '10434'),
('0e000000-0000-4000-8000-000000000007', 'Egnatia',         '110', 'Thessaloniki', 'Greece', '54622'),
('0e000000-0000-4000-8000-000000000008', 'Akti Miaouli',    '23',  'Piraeus',      'Greece', '18535'),
('0e000000-0000-4000-8000-000000000009', 'Riga Feraiou',    '18',  'Larissa',      'Greece', '41222'),
('0e000000-0000-4000-8000-00000000000a', 'Kolokotroni',     '55',  'Volos',        'Greece', '38221'),
('0e000000-0000-4000-8000-00000000000b', 'Nikis',           '31',  'Heraklion',    'Greece', '71202'),
('0e000000-0000-4000-8000-00000000000c', 'Dimokratias',     '9',   'Ioannina',     'Greece', '45332'),
('0e000000-0000-4000-8000-00000000000d', 'Kifisias',        '204', 'Athens',       'Greece', '15231'),
('0e000000-0000-4000-8000-00000000000e', 'Vasilissis Olgas','88',  'Thessaloniki', 'Greece', '54642'),
('0e000000-0000-4000-8000-00000000000f', 'Maizonos',        '37',  'Patras',       'Greece', '26222'),
('0e000000-0000-4000-8000-000000000010', 'Solomou',         '12',  'Chania',       'Greece', '73100'),
('0e000000-0000-4000-8000-000000000011', 'Papanastasiou',   '61',  'Rhodes',       'Greece', '85100'),
('0e000000-0000-4000-8000-000000000012', 'Filellinon',      '5',   'Kavala',       'Greece', '65302'),
('0e000000-0000-4000-8000-000000000013', 'Andrea Syngrou',  '143', 'Athens',       'Greece', '17121'),
('0e000000-0000-4000-8000-000000000014', 'Ethnikis Antistaseos', '27', 'Chalkida', 'Greece', '34100'),
('0e000000-0000-4000-8000-000000000015', 'Papanikoli',      '19',  'Serres',       'Greece', '62122'),
('0e000000-0000-4000-8000-000000000016', 'Anapafseos',      '3',   'Kalamata',     'Greece', '24100'),
('0e000000-0000-4000-8000-000000000017', 'Plastira',        '48',  'Trikala',      'Greece', '42131'),
('0e000000-0000-4000-8000-000000000018', 'Iroon Polytechniou', '72', 'Xanthi',     'Greece', '67100');

-- ---------------------------------------------------------------- members
INSERT INTO members (id, address_id, firstname, lastname, phone_number, email, birth_date, membership_date) VALUES
('0d000000-0000-4000-8000-000000000001', '0e000000-0000-4000-8000-000000000001', 'Maria',       'Ioannou',   '6912345677', 'maria.ioannou@gmail.com',      '1990-03-12', CURRENT_DATE - 720),
('0d000000-0000-4000-8000-000000000002', '0e000000-0000-4000-8000-000000000002', 'Nikos',       'Dimitriou', '6987654321', 'n.dimitriou@yahoo.gr',         '1985-07-23', CURRENT_DATE - 690),
('0d000000-0000-4000-8000-000000000003', '0e000000-0000-4000-8000-000000000003', 'Eleni',       'Papadaki',  '6900112233', 'eleni.papadaki@outlook.com',   '1996-11-02', CURRENT_DATE - 640),
('0d000000-0000-4000-8000-000000000004', '0e000000-0000-4000-8000-000000000004', 'George',      'Gakis',     '6972354763', 'g.gakis@yahoo.com',            '1978-01-30', CURRENT_DATE - 610),
('0d000000-0000-4000-8000-000000000005', '0e000000-0000-4000-8000-000000000005', 'Christopher', 'Kikeris',   '6972175849', 'chris.kik@gmail.com',          '2000-05-19', CURRENT_DATE - 560),
('0d000000-0000-4000-8000-000000000006', '0e000000-0000-4000-8000-000000000006', 'Sofia',       'Andreou',   '6944556677', 'sofia.andreou@gmail.com',      '1992-04-18', CURRENT_DATE - 520),
('0d000000-0000-4000-8000-000000000007', '0e000000-0000-4000-8000-000000000007', 'Dimitris',    'Vlachos',   '6933221100', 'd.vlachos@gmail.com',          '1988-09-05', CURRENT_DATE - 480),
('0d000000-0000-4000-8000-000000000008', '0e000000-0000-4000-8000-000000000008', 'Anna',        'Stefanou',  '6977889900', 'anna.stefanou@yahoo.gr',       '1999-12-11', CURRENT_DATE - 450),
('0d000000-0000-4000-8000-000000000009', '0e000000-0000-4000-8000-000000000009', 'Petros',      'Malamas',   '6955443322', 'p.malamas@outlook.com',        '1975-02-27', CURRENT_DATE - 410),
('0d000000-0000-4000-8000-00000000000a', '0e000000-0000-4000-8000-00000000000a', 'Katerina',    'Roussou',   '6966778899', 'k.roussou@gmail.com',          '1994-06-08', CURRENT_DATE - 380),
('0d000000-0000-4000-8000-00000000000b', '0e000000-0000-4000-8000-00000000000b', 'Yannis',      'Markou',    '6911223344', 'yannis.markou@gmail.com',      '1982-10-14', CURRENT_DATE - 340),
('0d000000-0000-4000-8000-00000000000c', '0e000000-0000-4000-8000-00000000000c', 'Ourania',     'Tsakiri',   '6988990011', 'o.tsakiri@yahoo.gr',           '2001-08-21', CURRENT_DATE - 300),
('0d000000-0000-4000-8000-00000000000d', '0e000000-0000-4000-8000-00000000000d', 'Alexandros',  'Sideris',   '6922334455', 'a.sideris@gmail.com',          '1987-03-09', CURRENT_DATE - 270),
('0d000000-0000-4000-8000-00000000000e', '0e000000-0000-4000-8000-00000000000e', 'Ioanna',      'Nikolaou',  '6944112233', 'ioanna.nikolaou@outlook.com',  '1993-07-16', CURRENT_DATE - 240),
('0d000000-0000-4000-8000-00000000000f', '0e000000-0000-4000-8000-00000000000f', 'Stelios',     'Karras',    '6977001122', 's.karras@yahoo.gr',            '1979-11-28', CURRENT_DATE - 210),
('0d000000-0000-4000-8000-000000000010', '0e000000-0000-4000-8000-000000000010', 'Despina',     'Fotiou',    '6933445566', 'despina.fotiou@gmail.com',     '1997-01-04', CURRENT_DATE - 180),
('0d000000-0000-4000-8000-000000000011', '0e000000-0000-4000-8000-000000000011', 'Vasilis',     'Antoniou',  '6966223344', 'v.antoniou@gmail.com',         '1984-05-22', CURRENT_DATE - 150),
('0d000000-0000-4000-8000-000000000012', '0e000000-0000-4000-8000-000000000012', 'Chrysa',      'Lambrou',   '6955667788', 'chrysa.lambrou@yahoo.gr',      '2002-02-13', CURRENT_DATE - 130),
('0d000000-0000-4000-8000-000000000013', '0e000000-0000-4000-8000-000000000013', 'Thanasis',    'Zervas',    '6911889900', 't.zervas@outlook.com',         '1991-09-30', CURRENT_DATE - 110),
('0d000000-0000-4000-8000-000000000014', '0e000000-0000-4000-8000-000000000014', 'Fotini',      'Alexiou',   '6988112233', 'f.alexiou@gmail.com',          '1998-12-07', CURRENT_DATE - 90),
('0d000000-0000-4000-8000-000000000015', '0e000000-0000-4000-8000-000000000015', 'Kostas',      'Mitsos',    '6922556677', 'kostas.mitsos@gmail.com',      '1973-04-25', CURRENT_DATE - 70),
('0d000000-0000-4000-8000-000000000016', '0e000000-0000-4000-8000-000000000016', 'Angeliki',    'Deli',      '6977334455', 'a.deli@yahoo.gr',              '1995-08-11', CURRENT_DATE - 50),
('0d000000-0000-4000-8000-000000000017', '0e000000-0000-4000-8000-000000000017', 'Michalis',    'Vardakis',  '6944778899', 'm.vardakis@outlook.com',       '1989-06-19', CURRENT_DATE - 30),
('0d000000-0000-4000-8000-000000000018', '0e000000-0000-4000-8000-000000000018', 'Rania',       'Kosta',     '6933667788', 'rania.kosta@gmail.com',        '2003-10-03', CURRENT_DATE - 12);

-- ---------------------------------------------------------------- rentals
-- Eight loans are open, three of them past their due date. The rest are history.
INSERT INTO rentals (id, member_id, copy_id, rental_date, due_date, return_date) VALUES
-- open and overdue
('0f000000-0000-4000-8000-000000000001', '0d000000-0000-4000-8000-000000000003', '0c000000-0000-4000-8000-00000000000d', NOW() - INTERVAL '48 days', NOW() - INTERVAL '18 days', NULL),
('0f000000-0000-4000-8000-000000000002', '0d000000-0000-4000-8000-000000000007', '0c000000-0000-4000-8000-000000000009', NOW() - INTERVAL '39 days', NOW() - INTERVAL '9 days',  NULL),
('0f000000-0000-4000-8000-000000000003', '0d000000-0000-4000-8000-00000000000b', '0c000000-0000-4000-8000-000000000023', NOW() - INTERVAL '33 days', NOW() - INTERVAL '3 days',  NULL),
-- open, still inside the loan period
('0f000000-0000-4000-8000-000000000004', '0d000000-0000-4000-8000-000000000005', '0c000000-0000-4000-8000-00000000002b', NOW() - INTERVAL '18 days', NOW() + INTERVAL '12 days', NULL),
('0f000000-0000-4000-8000-000000000005', '0d000000-0000-4000-8000-000000000001', '0c000000-0000-4000-8000-000000000001', NOW() - INTERVAL '12 days', NOW() + INTERVAL '18 days', NULL),
('0f000000-0000-4000-8000-000000000006', '0d000000-0000-4000-8000-000000000002', '0c000000-0000-4000-8000-00000000000e', NOW() - INTERVAL '8 days',  NOW() + INTERVAL '22 days', NULL),
('0f000000-0000-4000-8000-000000000007', '0d000000-0000-4000-8000-000000000006', '0c000000-0000-4000-8000-00000000000f', NOW() - INTERVAL '5 days',  NOW() + INTERVAL '25 days', NULL),
('0f000000-0000-4000-8000-000000000008', '0d000000-0000-4000-8000-00000000000a', '0c000000-0000-4000-8000-000000000019', NOW() - INTERVAL '2 days',  NOW() + INTERVAL '28 days', NULL),
-- returned
('0f000000-0000-4000-8000-000000000009', '0d000000-0000-4000-8000-000000000001', '0c000000-0000-4000-8000-000000000004', NOW() - INTERVAL '400 days', NOW() - INTERVAL '370 days', NOW() - INTERVAL '375 days'),
('0f000000-0000-4000-8000-00000000000a', '0d000000-0000-4000-8000-000000000002', '0c000000-0000-4000-8000-000000000006', NOW() - INTERVAL '380 days', NOW() - INTERVAL '350 days', NOW() - INTERVAL '356 days'),
('0f000000-0000-4000-8000-00000000000b', '0d000000-0000-4000-8000-000000000004', '0c000000-0000-4000-8000-000000000011', NOW() - INTERVAL '350 days', NOW() - INTERVAL '320 days', NOW() - INTERVAL '327 days'),
('0f000000-0000-4000-8000-00000000000c', '0d000000-0000-4000-8000-000000000005', '0c000000-0000-4000-8000-000000000013', NOW() - INTERVAL '320 days', NOW() - INTERVAL '290 days', NOW() - INTERVAL '292 days'),
('0f000000-0000-4000-8000-00000000000d', '0d000000-0000-4000-8000-000000000008', '0c000000-0000-4000-8000-000000000016', NOW() - INTERVAL '300 days', NOW() - INTERVAL '270 days', NOW() - INTERVAL '278 days'),
('0f000000-0000-4000-8000-00000000000e', '0d000000-0000-4000-8000-000000000009', '0c000000-0000-4000-8000-00000000001d', NOW() - INTERVAL '280 days', NOW() - INTERVAL '250 days', NOW() - INTERVAL '253 days'),
('0f000000-0000-4000-8000-00000000000f', '0d000000-0000-4000-8000-00000000000c', '0c000000-0000-4000-8000-00000000001e', NOW() - INTERVAL '260 days', NOW() - INTERVAL '230 days', NOW() - INTERVAL '236 days'),
('0f000000-0000-4000-8000-000000000010', '0d000000-0000-4000-8000-00000000000d', '0c000000-0000-4000-8000-000000000020', NOW() - INTERVAL '240 days', NOW() - INTERVAL '210 days', NOW() - INTERVAL '214 days'),
('0f000000-0000-4000-8000-000000000011', '0d000000-0000-4000-8000-00000000000e', '0c000000-0000-4000-8000-000000000022', NOW() - INTERVAL '220 days', NOW() - INTERVAL '190 days', NOW() - INTERVAL '199 days'),
('0f000000-0000-4000-8000-000000000012', '0d000000-0000-4000-8000-00000000000f', '0c000000-0000-4000-8000-000000000024', NOW() - INTERVAL '200 days', NOW() - INTERVAL '170 days', NOW() - INTERVAL '172 days'),
('0f000000-0000-4000-8000-000000000013', '0d000000-0000-4000-8000-000000000010', '0c000000-0000-4000-8000-000000000026', NOW() - INTERVAL '185 days', NOW() - INTERVAL '155 days', NOW() - INTERVAL '160 days'),
('0f000000-0000-4000-8000-000000000014', '0d000000-0000-4000-8000-000000000011', '0c000000-0000-4000-8000-000000000028', NOW() - INTERVAL '170 days', NOW() - INTERVAL '140 days', NOW() - INTERVAL '148 days'),
('0f000000-0000-4000-8000-000000000015', '0d000000-0000-4000-8000-000000000012', '0c000000-0000-4000-8000-00000000002a', NOW() - INTERVAL '155 days', NOW() - INTERVAL '125 days', NOW() - INTERVAL '131 days'),
('0f000000-0000-4000-8000-000000000016', '0d000000-0000-4000-8000-000000000013', '0c000000-0000-4000-8000-000000000002', NOW() - INTERVAL '140 days', NOW() - INTERVAL '110 days', NOW() - INTERVAL '112 days'),
('0f000000-0000-4000-8000-000000000017', '0d000000-0000-4000-8000-000000000014', '0c000000-0000-4000-8000-000000000005', NOW() - INTERVAL '125 days', NOW() - INTERVAL '95 days',  NOW() - INTERVAL '103 days'),
('0f000000-0000-4000-8000-000000000018', '0d000000-0000-4000-8000-000000000015', '0c000000-0000-4000-8000-000000000008', NOW() - INTERVAL '110 days', NOW() - INTERVAL '80 days',  NOW() - INTERVAL '86 days'),
('0f000000-0000-4000-8000-000000000019', '0d000000-0000-4000-8000-000000000016', '0c000000-0000-4000-8000-00000000000a', NOW() - INTERVAL '95 days',  NOW() - INTERVAL '65 days',  NOW() - INTERVAL '68 days'),
('0f000000-0000-4000-8000-00000000001a', '0d000000-0000-4000-8000-000000000017', '0c000000-0000-4000-8000-00000000000b', NOW() - INTERVAL '85 days',  NOW() - INTERVAL '55 days',  NOW() - INTERVAL '61 days'),
('0f000000-0000-4000-8000-00000000001b', '0d000000-0000-4000-8000-000000000018', '0c000000-0000-4000-8000-000000000012', NOW() - INTERVAL '75 days',  NOW() - INTERVAL '45 days',  NOW() - INTERVAL '47 days'),
('0f000000-0000-4000-8000-00000000001c', '0d000000-0000-4000-8000-000000000003', '0c000000-0000-4000-8000-000000000014', NOW() - INTERVAL '65 days',  NOW() - INTERVAL '35 days',  NOW() - INTERVAL '41 days'),
('0f000000-0000-4000-8000-00000000001d', '0d000000-0000-4000-8000-000000000006', '0c000000-0000-4000-8000-000000000015', NOW() - INTERVAL '55 days',  NOW() - INTERVAL '25 days',  NOW() - INTERVAL '29 days'),
('0f000000-0000-4000-8000-00000000001e', '0d000000-0000-4000-8000-000000000009', '0c000000-0000-4000-8000-000000000018', NOW() - INTERVAL '45 days',  NOW() - INTERVAL '15 days',  NOW() - INTERVAL '17 days'),
('0f000000-0000-4000-8000-00000000001f', '0d000000-0000-4000-8000-00000000000d', '0c000000-0000-4000-8000-00000000001b', NOW() - INTERVAL '40 days',  NOW() - INTERVAL '10 days',  NOW() - INTERVAL '14 days'),
('0f000000-0000-4000-8000-000000000020', '0d000000-0000-4000-8000-000000000010', '0c000000-0000-4000-8000-00000000002c', NOW() - INTERVAL '35 days',  NOW() - INTERVAL '5 days',   NOW() - INTERVAL '8 days');

-- ------------------------------------------------------------------ users
-- Same bcrypt hash as the default admin, so this account signs in with admin123!
INSERT INTO users (id, username, password, role_id)
SELECT '0aa00000-0000-4000-8000-000000000001',
       'librarian',
       '$2a$10$ZR4GplkmGSLORA0mnXktm.0EUO8L98aPa273x0kcbwZyAEjR2otSm',
       r.id
FROM roles r
WHERE r.name = 'LIBRARIAN';
