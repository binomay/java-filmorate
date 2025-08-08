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
    RATING      INTEGER references RATING (ID),
    RATE        INTEGER DEFAULT 0
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
    FILM_ID INTEGER not null references FILMS (ID),
    USER_ID INTEGER not null references USERS (ID),
    PRIMARY KEY (FILM_ID, USER_ID)
);

create table IF NOT EXISTS FRIENDSHIP
(
    USER_ID  INTEGER references USERS (ID),
    FRIEND   INTEGER references USERS (ID),
    ACCEPTED BOOLEAN default FALSE,
    PRIMARY KEY (USER_ID, FRIEND)
);

create table IF NOT EXISTS FILMSGENRE
(
    FILM_ID  INT NOT NULL REFERENCES FILMS (ID),
    GENRE_ID INT NOT NULL REFERENCES GENRE (ID),
    PRIMARY KEY (FILM_ID, GENRE_ID)
)



