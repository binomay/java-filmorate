package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.common.Numerator;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private HashMap<Integer, Film> filmsMap = new HashMap<>();
    private final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);

    @GetMapping
    public List<Film> getFilmsList() {
        log.info("Количество фильмов: " + filmsMap.size());
        return new ArrayList<>(filmsMap.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setId(Numerator.getFilmId());
        createOrUpdateFilm(film);
        log.info("Создан новый фильм: " + film.toString());
        return film;
    }

    @PutMapping
    Film updateFilm(@Valid @RequestBody Film film) {
        if (!filmsMap.containsKey(film.getId())) {
            String msg = "Не могу найти фильм с Id =" + film.getId();
            log.warn(msg);
            throw new ResourceNotFoundException(msg);
        }
        createOrUpdateFilm(film);
        log.info("Фильм Id = " + film.getId() + " изменен.");
        return film;
    }

    private void createOrUpdateFilm(Film film) {
        checkFilm(film);
        filmsMap.put(film.getId(), film);
    }

    private void checkFilm(Film film) {
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            String msg = "Дата релиза не может быть ранее дня рождения кино (28.12.1895";
            log.warn(msg);
            throw new ValidationException(msg);
        }
    }

}
