package ru.yandex.practicum.filmorate.common;

public class UserNumerator {
    private static int userId;

    public static int getUserId(){
        return ++ userId;
    }

}
