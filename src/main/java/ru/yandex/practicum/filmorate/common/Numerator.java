package ru.yandex.practicum.filmorate.common;

public class Numerator {
    private static int userId;
    private static int filmId;

    public static int getUserId(){
        return ++ userId;
    }

    public static int getFilmId(){
        return ++ filmId;
    }
}
