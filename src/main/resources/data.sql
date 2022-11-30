set mode mysql;
INSERT INTO RATING(ID, NAME)
VALUES (1, 'G') ON DUPLICATE KEY
UPDATE NAME = 'G';
INSERT INTO RATING(ID, NAME)
VALUES (2, 'PG') ON DUPLICATE KEY
UPDATE NAME = 'PG';
INSERT INTO RATING(ID, NAME)
VALUES (3, 'PG-13') ON DUPLICATE KEY
UPDATE NAME = 'PG-13';
INSERT INTO RATING(ID, NAME)
VALUES (4, 'R') ON DUPLICATE KEY
UPDATE NAME = 'R';
INSERT INTO RATING(ID, NAME)
VALUES (5, 'NC-17') ON DUPLICATE KEY
UPDATE NAME = 'NC-17';
INSERT INTO GENRE(ID, NAME)
VALUES (1, 'Комедия') ON DUPLICATE KEY
UPDATE NAME = 'Комедия';
INSERT INTO GENRE(ID, NAME)
VALUES (2, 'Драма') ON DUPLICATE KEY
UPDATE NAME = 'Драма';
INSERT INTO GENRE(ID, NAME)
VALUES (3, 'Мультфильм') ON DUPLICATE KEY
UPDATE NAME = 'Мультфильм';
INSERT INTO GENRE(ID, NAME)
VALUES (4, 'Триллер') ON DUPLICATE KEY
UPDATE NAME = 'Триллер';
INSERT INTO GENRE(ID, NAME)
VALUES (5, 'Документальный') ON DUPLICATE KEY
UPDATE NAME = 'Документальный';
INSERT INTO GENRE(ID, NAME)
VALUES (6, 'Боевик') ON DUPLICATE KEY
UPDATE NAME = 'Боевик';
COMMIT;