package ru.yandex.practicum.filmorate.service.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
public class RatingService {

    private RatingStorage storage;

    public RatingService(RatingStorage storage) {
        this.storage = storage;
    }

    public Rating getRatingById(int ratingId) {
        return storage.getRatingById(ratingId)
                .orElseThrow(() -> {
                    String msg = "Не нашел рейтинг с Id = " + ratingId;
                    log.warn(msg);
                    throw new ResourceNotFoundException(msg);
                });
    }

    public List<Rating> getAllRatings() throws SQLException {
        return storage.getAllRatings();
    }

}
