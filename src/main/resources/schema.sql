create table IF NOT EXISTS GENRE
(
    ID   INTEGER               not null
        primary key,
    NAME CHARACTER VARYING(20) not null
);

create table IF NOT EXISTS RATING
(
    ID   INTEGER               not null primary key,
    NAME CHARACTER VARYING(20) not null
);

create table IF NOT EXISTS FILMS
(
    ID          INTEGER                not null primary key,
    NAME        CHARACTER VARYING(100) not null,
    DESCRIPTION CHARACTER VARYING(200),
    RELEASEDATE DATE                   not null,
    DURATION    INTEGER                not null,
    RATING      INTEGER references RATING (ID)
);

create table IF NOT EXISTS USERS
(
    ID        INTEGER               not null primary key,
    EMAIL     CHARACTER VARYING(50) not null,
    LOGIN     CHARACTER VARYING(50) not null,
    NAME      CHARACTER VARYING(50) not null,
    BIRTHDATE DATE                  not null
);

create table IF NOT EXISTS FILMLIKES
(
    ID      INTEGER auto_increment primary key,
    FILM_ID INTEGER not null references FILMS (ID),
    USER_ID INTEGER not null references USERS (ID)
);

create table IF NOT EXISTS FRIENDSHIP
(
    ID       INTEGER auto_increment primary key,
    USER_ID  INTEGER references USERS (ID),
    FRIEND   INTEGER references USERS (ID),
    ACCEPTED BOOLEAN default FALSE
);

create table IF NOT EXISTS FILMSGENRE
(
    ID       INT PRIMARY KEY AUTO_INCREMENT,
    FILM_ID  INT NOT NULL REFERENCES FILMS (ID),
    GENRE_ID INT NOT NULL REFERENCES GENRE (ID)
)



