package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.numerators.UserNumerator;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        log.info("Количество пользователей: " + users.size());
        return new ArrayList<User>(users.values());
    }

    @Override
    public User createUser(User user) {
        user.setId(UserNumerator.getUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUserById(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void addFriendToUser(User user, int friendId) {
        User newUser = updateUser(user);
    }

    @Override
    public void deleteFriendFromUser(User user, User friend) {
        User newUser = updateUser(user);
    }

    @Override
    public List<User> getFriendsList(User user) {
        return user.getFriendsList().stream().map(x -> getUserById(x).get()).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        User user1 = getUserById(id).orElseThrow(() -> {
            String msg = "Не нашел пользователя с Id = " + id;
            log.warn(msg);
            throw new ResourceNotFoundException(msg);
        });;
        User user2 = getUserById(otherId).orElseThrow(() -> {
            String msg = "Не нашел пользователя с Id = " + otherId;
            log.warn(msg);
            throw new ResourceNotFoundException(msg);
        });;
        Set<Integer> set1 = user1.getFriendsList();
        Set<Integer> set2 = user2.getFriendsList();
        return set1.stream().filter(u -> set2.contains(u)).map(u -> getUserById(u).get()).collect(Collectors.toList());
    }
}
