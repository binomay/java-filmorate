package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.numerators.UserNumerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("dbUserStorage")
public class DBUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public DBUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM USERS";
        List<User> userList = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
        for (User user : userList) {
            addAllFriendsToUser(user);
        }
        return userList;
    }

    @Override
    public Optional<User> getUserById(int userId) {
        return commonGetUserById(userId, true);
    }

    @Override
    public User createUser(User user) {
        user.setId(UserNumerator.getUserId());
        String sql = "insert into users(id, email, login, name, birthdate) " +
                "values (?, ?, ?,?,?)";
        jdbcTemplate.update(sql,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE USERS SET " +
                "EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDATE = ? " +
                " WHERE ID = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void addFriendToUser(User user, User friend) {
        String sql = "INSERT INTO FRIENDSHIP(USER_ID, FRIEND, ACCEPTED) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getId(),
                friend.getId(),
                false);
    }

    @Override
    public void deleteFriendFromUser(User user, User friend) {
        String sql = "DELETE FROM FRIENDSHIP WHERE " +
                "USER_ID = ? AND FRIEND = ?";
        jdbcTemplate.update(sql,
                user.getId(),
                friend.getId());
    }

    @Override
    public List<User> getFriendsList(User user) {
        String sql = "SELECT * FROM FRIENDSHIP WHERE USER_ID = " + user.getId();
        return jdbcTemplate.query(sql, (rs, rowNum) -> getFriend(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthdate").toLocalDate());
        return user;
    }

    private void addAllFriendsToUser(User user) {
        List<User> friendList = getFriendsList(user);
        for (User friend : friendList) {
            user.addFriend(friend);
        }
    }

    private User getFriend(ResultSet rs) throws SQLException {
        int id = rs.getInt("friend");
        return commonGetUserById(id, false).get();
    }

    private Optional<User> commonGetUserById(int userId, boolean needFriendList) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE ID = ?", userId);
        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthdate").toLocalDate());
            if (needFriendList) {
                addAllFriendsToUser(user);
            }
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", userId);
            return Optional.empty();
        }
    }

}
