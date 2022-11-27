package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.numerators.FilmNumerator;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("DBFilmStorage")
public class DBFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;


    public DBFilmStorage(JdbcTemplate jdbcTemplate, RatingStorage ratingStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingStorage = ratingStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Film> getFilmsList() {
        String sql = "SELECT * FROM FILMS";
        return commonGetFilmList(sql);
    }

    @Override
    public List<Film> getPrioritizedFilmList() {
        String sql = "SELECT F.*  FROM FILMS F LEFT OUTER JOIN FILMLIKES Fl on FL.FILM_ID = F.ID" +
                "          group by f.id order by count(FL.USER_ID) desc";
        return commonGetFilmList(sql);
    }


    @Override
    public Film createFilm(Film film) {
        film.setId(FilmNumerator.geFilmId());
        String sql = "INSERT INTO FILMS (id, name, description, releasedate, duration, rating) "
                + "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());

        insertAllGenresToFilm(film);
        //return film;
        return getFilmById(film.getId()).get();
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILMS SET "
                + "NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?, RATING = ? WHERE ID = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateAllGenreToFilm(film);
        //return film;
        return getFilmById(film.getId()).get();
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        String sql = "SELECT * FROM FILMS WHERE ID = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, filmId);
        if (rs.next()) {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("releasedate").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            int mpaId = rs.getInt("rating");
            film.setMpa(ratingStorage.getRatingById(mpaId).get());
            setAllGenresToFilm(film);
            setAllLikesToFilm(film);
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return Optional.of(film);
        } else {
            log.info("Фильм с идентификатором {} не найден.", filmId);
            return Optional.empty();
        }
    }

    @Override
    public void addLikeToFilm(Film film, User user) {
        String sql = "INSERT INTO FILMLIKES(FILM_ID, USER_ID) VALUES(?,?)";
        jdbcTemplate.update(sql, film.getId(), user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sql = "DELETE FROM FILMLIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, film.getId(), user.getId());
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("releasedate").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        int mpaId = rs.getInt("rating");
        film.setMpa(ratingStorage.getRatingById(mpaId).get());
        setAllGenresToFilm(film);
        setAllLikesToFilm(film);
        return film;
    }

    private void setAllLikesToFilm(Film film) {
        List<Integer> likeIdList = getLikeList(film);
        for (Integer user_id : likeIdList) {
            film.setLike(user_id);
        }
    }

    private List<Integer> getLikeList(Film film) {
        String sql = "SELECT USER_ID FROM FILMLIKES WHERE FILM_ID = " + film.getId();
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("USER_ID"));
    }

    private void setAllGenresToFilm(Film film) {
        for (Genre genre : genreStorage.getGenreListForFilm(film)) {
            film.setGenre(genre);
        }
    }

    private List<Film> commonGetFilmList(String sql) {
        List<Film> filmList = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        for (Film film : filmList) {
            setAllLikesToFilm(film);
        }
        return filmList;
    }

    private void insertAllGenresToFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            insertOneGenreToFilm(film.getId(), genre.getId());
        }
    }

    private void insertOneGenreToFilm(int filmId, int genreId) {
        String sql = "INSERT INTO FILMSGENRE(FILM_ID, GENRE_ID) VALUES (?,?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    private void updateAllGenreToFilm(Film film) {
        deleteAllGenresToFilm(film);
        insertAllGenresToFilm(film);
    }

    private void deleteAllGenresToFilm(Film film) {
        String sql = "DELETE FROM FILMSGENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
    }

}
