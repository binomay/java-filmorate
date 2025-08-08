package ru.yandex.practicum.filmorate.numerators;

public class FilmNumerator {
    private static int filmId;

    public static int geFilmId() {
        return ++filmId;
    }
}
