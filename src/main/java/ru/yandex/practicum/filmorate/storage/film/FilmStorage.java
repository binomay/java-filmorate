package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getFilmsList();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLikeToFilm(Film film, User user);

    void deleteLike(Film film, User user);

    Optional<Film> getFilmById(int filmId);

    List<Film> getPrioritizedFilmList(int maxCountFilms);

}
