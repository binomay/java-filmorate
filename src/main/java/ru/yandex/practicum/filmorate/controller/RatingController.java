package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.rating.RatingService;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService service;

    public RatingController(RatingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Rating> getAllRatings() throws SQLException {
        return service.getAllRatings();
    }

    @GetMapping("/{id}")
    public Rating getRatingById(@PathVariable int id) {
        return service.getRatingById(id);
    }
}
