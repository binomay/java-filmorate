package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Optional<Genre> getGenreById(int idGenre);

    List<Genre> getAllGenres() throws SQLException;

    List<Genre> getGenreListForFilm(Film film);
}
