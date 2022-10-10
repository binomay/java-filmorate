package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.numerators.UserNumerator;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();

    public List<User> getUsers() {
        log.info("Количество пользователей: " + users.size());
        return new ArrayList<User>(users.values());
    }

    public User createUser(User user) {
        user.setId(UserNumerator.getUserId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> getUserById(int userId) {
        return Optional.ofNullable(users.get(userId));
    }
}
