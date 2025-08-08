package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.numerators.FilmNumerator;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Qualifier("DBFilmStorage")
public class DBFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    public DBFilmStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Film> getFilmsList() {
        String sql = getSqlForFilmList();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        return commonGetFilmList(rs);

    }

    @Override
    public List<Film> getPrioritizedFilmList(int maxCountFilms) {
        String sql = "SELECT F.*,  R.ID MPA_ID, R.NAME MPA_NAME "
                + "FROM FILMS F LEFT JOIN RATING R ON F.RATING = R.ID "
                + "ORDER BY F.RATE  DESC LIMIT " + maxCountFilms;
        return getPopularFilmList(jdbcTemplate.queryForRowSet(sql));
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(FilmNumerator.geFilmId());
        String sql = "INSERT INTO FILMS (id, name, description, releasedate, duration, rating) "
                + "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        insertAllGenresToFilm(film);
        return getFilmById(film.getId()).get();
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILMS SET "
                + "NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?, RATING = ? WHERE ID = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateAllGenreToFilm(film);
        return getFilmById(film.getId()).get();
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        String sql = getSqlForFilmList() + " WHERE F.ID = " + filmId;
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        List<Film> filmList = commonGetFilmList(rs);
        if (filmList.size() > 0) {
            return Optional.of(filmList.get(0));
        } else {
            log.info("Фильм с идентификатором {} не найден.", filmId);
            return Optional.empty();
        }
    }

    private List<Film> commonGetFilmList(SqlRowSet rs) {
        Map<Integer, Film> filmsMap = new HashMap<>();
        while (rs.next()) {
            int filmId;
            Film currentFilm;
            filmId = rs.getInt("FILM_ID");
            if (!filmsMap.containsKey(filmId)) {
                currentFilm = makeFilmFromRs(rs);
            } else {
                currentFilm = filmsMap.get(filmId);
            }
            if (rs.getInt("GENRE_ID") != 0) {
                Genre genre = makeGenreFromRs(rs);
                if (!currentFilm.getGenres().contains(genre)) {
                    currentFilm.addGenre(genre);
                }
            }
            if (rs.getInt("LIKE_USER_ID") != 0) {
                currentFilm.addLike(rs.getInt("LIKE_USER_ID"));
            }
            filmsMap.put(filmId, currentFilm);
        }
        return new ArrayList<>(filmsMap.values());
    }

    private String getSqlForFilmList() {
        return "SELECT F.ID FILM_ID, "
                + " F.NAME FILM_NAME, "
                + "F.DESCRIPTION FILM_DESCRIPTION, "
                + "F.RELEASEDATE FILM_DATE, "
                + "F.DURATION FILM_DURATION, "
                + "FL.USER_ID LIKE_USER_ID, "
                + "GENRE.ID   GENRE_ID, "
                + "GENRE.NAME GENRE_NAME, "
                + "RATING.ID MPA_ID, "
                + "RATING.NAME MPA_NAME  "
                + "FROM FILMS F  "
                + "LEFT JOIN FILMLIKES Fl on FL.FILM_ID = F.ID "
                + "LEFT JOIN FILMSGENRE FG on F.ID = FG.FILM_ID "
                + "lEFT JOIN GENRE ON FG.GENRE_ID = GENRE.ID "
                + "LEFT JOIN RATING  on F.RATING = RATING.ID";
    }

    @Override
    public void addLikeToFilm(Film film, User user) {
        String sql = "INSERT INTO FILMLIKES(FILM_ID, USER_ID) VALUES(?,?)";
        jdbcTemplate.update(sql, film.getId(), user.getId());
        sql = "UPDATE FILMS SET RATE = RATE + 1 WHERE ID = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sql = "DELETE FROM FILMLIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, film.getId(), user.getId());
        sql = "UPDATE FILMS SET RATE = RATE - 1 WHERE ID = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    private Film makeFilmFromRs(SqlRowSet rs) {
        Film film = new Film();
        film.setId(rs.getInt("FILM_ID"));
        film.setName(rs.getString("FILM_NAME"));
        film.setDescription(rs.getString("FILM_DESCRIPTION"));
        film.setReleaseDate(rs.getDate("FILM_DATE").toLocalDate());
        film.setDuration(rs.getInt("FILM_DURATION"));
        film.setMpa(new Rating(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")));
        return film;
    }

    private Genre makeGenreFromRs(SqlRowSet rs) {
        return new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME"));
    }

    private void insertAllGenresToFilm(Film film) {
        jdbcTemplate.batchUpdate("INSERT INTO FILMSGENRE(FILM_ID, GENRE_ID) VALUES (?,?)",
                new BatchPreparedStatementSetter() {
                    private List<Genre> tmpList = new ArrayList<>(film.getGenres());

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, tmpList.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                });
    }

    private void updateAllGenreToFilm(Film film) {
        deleteAllGenresToFilm(film);
        insertAllGenresToFilm(film);
    }

    private void deleteAllGenresToFilm(Film film) {
        String sql = "DELETE FROM FILMSGENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    //TODO: СДЕЛАТЬ
    private List<Film> getPopularFilmList(SqlRowSet rs) {
        Map<Integer, Film> filmsMap = new HashMap<>();
        String sql = "SELECT F.ID FILM_ID, FL.USER_ID, G.ID GENRE_ID, G.NAME GENRE_NAME " +
                "FROM FILMS F " +
                "LEFT JOIN FILMLIKES FL ON F.ID = FL.FILM_ID " +
                "LEFT JOIN FILMSGENRE FG ON F.ID = FG.FILM_ID " +
                "LEFT JOIN GENRE G ON FG.GENRE_ID = G.ID WHERE F.ID IN (";
        while (rs.next()) {
            Film film = makeFilmForPopularList(rs);
            filmsMap.put(film.getId(), film);
            sql = sql + film.getId() + ", ";
        }
        sql = sql.substring(0, sql.length() - 2) + ")";
        SqlRowSet rs1 = jdbcTemplate.queryForRowSet(sql);
        while (rs1.next()) {
            int filmId = rs1.getInt("FILM_ID");
            int userId = rs1.getInt("USER_ID");
            if (userId != 0) {
                filmsMap.get(filmId).addLike(userId);
            }
            int genreId = rs1.getInt("GENRE_ID");
            if (genreId != 0) {
                filmsMap.get(filmId).addGenre(new Genre(genreId, rs1.getString("GENRE_NAME")));
            }
        }
        return new ArrayList<>(filmsMap.values());
    }

    private Film makeFilmForPopularList(SqlRowSet rs) {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("releasedate").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(new Rating(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")));
        return film;
    }

    private List<Integer> getLikeList(Film film) {
        String sql = "SELECT USER_ID FROM FILMLIKES WHERE FILM_ID = " + film.getId();
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("USER_ID"));
    }
}
