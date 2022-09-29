package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.common.UserNumerator;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        log.info("Количество пользователей: " + users.size());
        return new ArrayList<User>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        user.setId(UserNumerator.getUserId());
        createOrUpdateUser(user);
        log.info("Создан новый пользователь: " + user.toString());
        return user;

    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if(!users.containsKey(user.getId())) {
            String msg = "Не нашел пользователя с Id = " + user.getId();
            log.warn(msg);
            throw new ResourceNotFoundException(msg);
        }
        createOrUpdateUser(user);
        log.info("Пользователь Id = " + user.getId() + " изменен.");
        return user;
    }

    public void createOrUpdateUser(User user) {
        checkLogin(user.getLogin());
        //имя не может быть пустым, если пустое - использовать login
        if(user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
    }

    private void checkLogin(String login) {
        if(login.contains(" ")) {
            String msg = "Логин не может содержать пробелы!";
            log.warn(msg);
            throw new ValidationException(msg);
        }
    }
}
