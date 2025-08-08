package ru.yandex.practicum.filmorate.numerators;

public class UserNumerator {
    private static int userId;

    public static int getUserId() {
        return ++userId;
    }

}
