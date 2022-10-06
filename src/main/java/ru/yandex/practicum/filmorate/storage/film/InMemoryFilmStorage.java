package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.comparators.FilmComparatorByLikes;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.numerators.FilmNumerator;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private HashMap<Integer, Film> filmsMap = new HashMap<>();

    //будем хранить отсортированные по количеству лаков фильмы
    private TreeSet<Film> prioritizedFilmsByLikes = new TreeSet<>(new FilmComparatorByLikes());

    public Optional<Film> getFilmById(int filmId) {
        return Optional.ofNullable(filmsMap.get(filmId));
    }

    public List<Film> getFilmsList() {
        log.info("Количество фильмов: " + filmsMap.size());
        return new ArrayList<>(filmsMap.values());
    }

    public Film createFilm(Film film) {
        createOrUpdateFilm(film, true);
        return film;
    }

    public Film updateFilm(Film film) {
        createOrUpdateFilm(film, false);
        return film;
    }

    private void createOrUpdateFilm(Film film, boolean isCreate) {
        if (isCreate) {
            film.setId(FilmNumerator.geFilmId());
        }
        filmsMap.put(film.getId(), film);
        prioritizedFilmsByLikes.add(film);
    }

    public List<Film> getPrioritizedFilmList() {
        return new ArrayList<Film>(prioritizedFilmsByLikes);
    }

    public void deleteFromPriorityList(Film film) {
        prioritizedFilmsByLikes.remove(film);
    }
}
