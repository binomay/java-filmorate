package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    //добавление в друзья,
    // удаление из друзей,
    //вывод списка общих друзей
    private UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    //в нем логики никакой нет, тупо проксируем
    public List<User> getUsers() {
        return storage.getUsers();
    }

    public User getUserById(int userId) {
        Optional<User> optUser = storage.getUserById(userId);
        if (!optUser.isPresent()) {
            String msg = "Не нашел пользователя с Id = " + userId;
            log.warn(msg);
            throw new ResourceNotFoundException(msg);
        } else {
            return optUser.get();
        }
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
        //вытащить список френдов... он может потеряться при апдейте
        //User oldUser = storage.getUserById().get();
        //oldUser.getFriendsList()
        user = storage.updateUser(user);
        log.info("Пользователь Id = " + user.getId() + " изменен.");
        return user;
    }

    public void addFriendToUser(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.addFriend(friend);
        friend.addFriend(user);
        storage.updateUser(user);
        storage.updateUser(friend);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.deleteFriend(friend);
        friend.deleteFriend(user);
        storage.updateUser(user);
        storage.updateUser(friend);
    }

    public List<User> getFriendsList(int userId) {
        User user = getUserById(userId);
        return user.getFriendsList().stream().map(x -> getUserById(x)).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        User user1 = getUserById(id);
        User user2 = getUserById(otherId);
        List<User> list1 = getFriendsList(user1.getId());
        List<User> list2 = getFriendsList(user2.getId());
        return list1.stream().filter(u -> list2.contains(u)).collect(Collectors.toList());
    }

    private void checkUserAndOptionalUpdate(User user) {
        checkLogin(user);
        checkAndOptionalUpdateUserName(user);
    }

    private void checkLogin(User user) {
        if (user.getLogin().contains(" ")) {
            String msg = "Логин не может содержать пробелы!";
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
