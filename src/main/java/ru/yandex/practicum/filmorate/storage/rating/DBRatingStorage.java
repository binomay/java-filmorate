package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class DBRatingStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    public DBRatingStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Rating> getRatingById(int idRating) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATING WHERE ID = ?", idRating);
        if (userRows.next()) {
            Rating rating = new Rating(
                    userRows.getInt("id"),
                    userRows.getString("name")
            );

            log.info("Найден нейтинг: {} {}", rating.getId(), rating.getName());
            return Optional.of(rating);
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", idRating);
            return Optional.empty();
        }
    }

    @Override
    public List<Rating> getAllRatings() {
        String sql = "SELECT * FROM RATING";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeRating(rs));
    }

    private Rating makeRating(ResultSet rs) throws SQLException {
        return new Rating(rs.getInt("id"), rs.getString("name"));
    }
}
