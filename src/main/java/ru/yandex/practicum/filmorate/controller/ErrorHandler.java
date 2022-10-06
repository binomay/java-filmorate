package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Map;

@RestControllerAdvice("ru.yandex.practicum.filmorate")
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidateException(final ValidationException e) {
        return Map.of(
                "error", "Ошибка при выполнении запроса: " + ValidationException.class.getSimpleName(),
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Map<String, String> handleResourceNotFound(ResourceNotFoundException e) {
        return Map.of(
                "error", "Ошибка при выполнении запроса: " + ResourceNotFoundException.class.getSimpleName(),
                "errorMessage", e.getMessage()
        );
    }
}
