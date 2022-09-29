package ru.yandex.practicum.filmorate.common;

public class FilmNumerator {

    private static int filmId;

    public static int geFilmId(){
        return ++ filmId;
    }
}
