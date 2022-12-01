package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@lombok.Data
public class User {
    /*
    логин не может быть пустым и содержать пробелы;
    имя для отображения может быть пустым — в таком случае будет использован логин;
     */
    private int id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;

    private final Set<Integer> friendsList = new HashSet<>();

    public void addFriend(int  friendId) {
        friendsList.add(friendId);
    }

    public void deleteFriend(User friend) {
        friendsList.remove(friend.getId());
    }
}
