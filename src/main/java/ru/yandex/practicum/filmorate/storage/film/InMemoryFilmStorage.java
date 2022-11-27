package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.comparators.FilmComparatorByLikes;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.numerators.FilmNumerator;

import java.util.*;

@Slf4j
@Component
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private HashMap<Integer, Film> filmsMap = new HashMap<>();

    @Override
    public Optional<Film> getFilmById(int filmId) {
        return Optional.ofNullable(filmsMap.get(filmId));
    }

    @Override
    public List<Film> getFilmsList() {
        log.info("Количество фильмов: " + filmsMap.size());
        return new ArrayList<>(filmsMap.values());
    }

    @Override
    public Film createFilm(Film film) {
        createOrUpdateFilm(film, true);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        createOrUpdateFilm(film, false);
        return film;
    }

    @Override
    public void addLikeToFilm(Film film, User user) {
        film.setLike(user.getId());
        updateFilm(film);
    }

    @Override
    public void deleteLike(Film film, User user) {
        film.deleteLike(user);
        updateFilm(film);
    }

    private void createOrUpdateFilm(Film film, boolean isCreate) {
        if (isCreate) {
            film.setId(FilmNumerator.geFilmId());
        }
        filmsMap.put(film.getId(), film);
    }

    public List<Film> getPrioritizedFilmList() {
        List<Film> listFilms = new ArrayList<Film>(filmsMap.values());
        Collections.sort(listFilms, new FilmComparatorByLikes());
        return listFilms;
    }

}
