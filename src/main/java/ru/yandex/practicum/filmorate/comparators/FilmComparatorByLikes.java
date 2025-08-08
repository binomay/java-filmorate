package ru.yandex.practicum.filmorate.comparators;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmComparatorByLikes implements Comparator<Film> {
    @Override
    public int compare(Film film1, Film film2) {
        if (film1.giveCountLikes() == film2.giveCountLikes()) {
            return film1.getId() - film2.getId();
        } else {
            return film2.giveCountLikes() - film1.giveCountLikes();
        }
    }
}