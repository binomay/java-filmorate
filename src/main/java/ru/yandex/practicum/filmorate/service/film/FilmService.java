package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserService userService;
    private final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(@Qualifier("DBFilmStorage") FilmStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    public List<Film> getFilmsList() {
        return storage.getFilmsList();
    }

    public Film createFilm(Film film) {
        checkReleaseDate(film);
        film = storage.createFilm(film);
        log.info("Создан новый фильм: " + film.toString());
        return film;
    }

    public Film updateFilm(Film film) {
        checkFilmExists(film);
        checkReleaseDate(film);
        film = storage.updateFilm(film);
        log.info("Фильм Id = " + film.getId() + " изменен.");
        return film;
    }

    public void addLike(int filmId, int userId) {
        User user = userService.getUserById(userId);
        Film film = getFilmById(filmId);
        storage.addLikeToFilm(film, user);
    }

    public void deleteLike(int filmId, int userId) {
        User user = userService.getUserById(userId);
        Film film = getFilmById(filmId);
        storage.deleteLike(film, user);
    }

    public Film getFilmById(int filmId) {
        return storage.getFilmById(filmId)
                .orElseThrow(() -> {
                    String msg = "Не могу найти фильм с Id =" + filmId;
                    log.warn(msg);
                    throw new ResourceNotFoundException(msg);
                });
    }

    public List<Film> getTopFilmsByLikes(int maxCountFilms) {
        if (maxCountFilms <= 0) {
            throw new ValidationException("Количетво лайков не может быть отрицательным!");
        }
        return storage.getPrioritizedFilmList(maxCountFilms);
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            String msg = "Дата релиза не может быть ранее дня рождения кино (28.12.1895)";
            log.warn(msg);
            throw new ValidationException(msg);
        }
    }

    private void checkFilmExists(Film film) {
        getFilmById(film.getId());
    }
}
