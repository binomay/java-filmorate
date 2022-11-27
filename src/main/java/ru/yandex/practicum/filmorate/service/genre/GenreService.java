package ru.yandex.practicum.filmorate.service.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
public class GenreService {

    private GenreStorage storage;

    public GenreService(GenreStorage genreStorage) {
        this.storage = genreStorage;
    }

    public Genre getGenreById(int genreId) {
        return storage.getGenreById(genreId)
                .orElseThrow(() -> {
                    String msg = "Не нашел жанр с Id = " + genreId;
                    log.warn(msg);
                    throw new ResourceNotFoundException(msg);
                });
    }

    public List<Genre> getAllGenres() throws SQLException {
        return storage.getAllGenres();
    }
}
