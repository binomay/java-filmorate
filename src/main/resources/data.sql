set mode mysql;
--ПОДЧИСТИМ, ИНАЧЕ ПОСТМАН НЕ ПРХОДИТ
DELETE
FROM FRIENDSHIP
-- используем if in (select...), т.к. script почему-то отказывается выполнять delete без условия where....
WHERE ID IN (SELECT ID FROM FRIENDSHIP);
DELETE
FROM FILMLIKES
WHERE ID IN (SELECT ID FROM FILMLIKES);
DELETE
FROM FILMSGENRE
WHERE ID IN (SELECT ID FROM FILMSGENRE);
DELETE
FROM USERS
WHERE ID IN (SELECT ID FROM USERS);
DELETE
FROM FILMS
WHERE ID IN (SELECT ID FROM FILMS);
DELETE
FROM GENRE
WHERE ID IN (SELECT ID FROM GENRE);
DELETE
FROM RATING
WHERE ID IN (SELECT ID FROM RATING);
-- НА ВСЯКИЙ СЛУЧАЙ
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