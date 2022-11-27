package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
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
    public void addFriendToUser(User user, User friend) {
        User newUser = updateUser(user);
        //начиная с 11 спринта дружба одностороннаяя
        //User newFriend = updateUser(friend);
    }

    @Override
    public void deleteFriendFromUser(User user, User friend) {
        User newUser = updateUser(user);
        //начиная с 11 спринта дружба одностороннаяя
        //User newFriend = updateUser(friend);
    }

    @Override
    public List<User> getFriendsList(User user) {
        return user.getFriendsList().stream().map(x -> getUserById(x).get()).collect(Collectors.toList());
    }
}
