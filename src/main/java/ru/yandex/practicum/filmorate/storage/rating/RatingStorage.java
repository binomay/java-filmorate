package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RatingStorage {

    Optional<Rating> getRatingById(int idRating);

    List<Rating> getAllRatings() throws SQLException;
}
