package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("dbUserStorage") UserStorage storage) {
        this.storage = storage;
    }

    public List<User> getUsers() {
        return storage.getUsers();
    }

    public User getUserById(int userId) {
        return storage.getUserById(userId)
                .orElseThrow(() -> {
                    String msg = "Не нашел пользователя с Id = " + userId;
                    log.warn(msg);
                    throw new ResourceNotFoundException(msg);
                });
    }

    public User createUser(User user) {
        checkUserAndOptionalUpdate(user);
        user = storage.createUser(user);
        log.info("Создан новый пользователь: " + user.toString());
        return user;
    }

    public User updateUser(User user) {
        checkUserExists(user);
        checkUserAndOptionalUpdate(user);
        user = storage.updateUser(user);
        log.info("Пользователь Id = " + user.getId() + " изменен.");
        return user;
    }

    public void addFriendToUser(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.addFriend(friend);
        storage.addFriendToUser(user, friend);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.deleteFriend(friend);
        storage.deleteFriendFromUser(user, friend);
    }

    public List<User> getFriendsList(int userId) {
        User user = getUserById(userId);
        return storage.getFriendsList(user);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return storage.getCommonFriends(id, otherId);
    }

    private void checkUserAndOptionalUpdate(User user) {
        checkLogin(user);
        checkBirthDate(user);
        checkAndOptionalUpdateUserName(user);
    }

    private void checkLogin(User user) {
        if (user.getLogin().contains(" ")) {
            String msg = "Логин не может содержать пробелы!";
            log.warn(msg);
            throw new ValidationException(msg);
        }
    }

    private void checkBirthDate(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String msg = "Дата рождения не может быть в будущем!!";
            log.warn(msg);
            throw new ValidationException(msg);
        }
    }

    private void checkAndOptionalUpdateUserName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private void checkUserExists(User user) {
        User tmpUser = getUserById(user.getId());
    }

}
