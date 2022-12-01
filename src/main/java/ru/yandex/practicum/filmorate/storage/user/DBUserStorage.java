package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.numerators.UserNumerator;

import java.util.*;

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
        String sql = getSqlForUserList();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        return commonGetUserList(rs);

    }

    public List<User> getCommonFriends(int id, int otherId) {
        String sql = "SELECT U.ID USER_ID, U.EMAIL USER_EMAIL, U.LOGIN USER_LOGIN, " +
                "U.NAME USER_NAME, U.BIRTHDATE USER_DATE, " +
                "F.ID FRIEND_ID,  F.EMAIL FRIEND_EMAIL, F.LOGIN FRIEND_LOGIN, " +
                "F.NAME FRIEND_NAME, F.BIRTHDATE FRIEND_DATE " +
                "FROM FRIENDSHIP FS1 JOIN  FRIENDSHIP FS2 ON FS1.FRIEND = FS2.FRIEND AND FS1.ACCEPTED = TRUE AND FS2.ACCEPTED = TRUE " +
                "JOIN USERS U ON U.ID = FS1.FRIEND " +
                "LEFT JOIN FRIENDSHIP FS3 ON FS3.USER_ID = U.ID AND FS3.ACCEPTED = TRUE " +
                "LEFT JOIN USERS F ON FS3.FRIEND = U.ID AND FS3.ACCEPTED = TRUE " +
                "WHERE FS1.USER_ID = ? AND FS2.USER_ID = ? ";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id, otherId);
        return commonGetUserList(rs);
    }

    @Override
    public Optional<User> getUserById(int userId) {
        String sql = getSqlForUserList() + "  WHERE U.ID = " + userId;
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        List<User> userList = commonGetUserList(rs);
        if (userList.size() > 0) {
            return Optional.of(userList.get(0));
        } else {
            log.info("User с идентификатором {} не найден.", userId);
            return Optional.empty();
        }


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
    public void addFriendToUser(User user, int friendId) {
        try {
            String sql = "INSERT INTO FRIENDSHIP(USER_ID, FRIEND, ACCEPTED) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sql,
                    user.getId(),
                    friendId,
                    true);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Не найден пользователь с Id: " + friendId);
        }
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
        String sql = "SELECT U.ID USER_ID, U.EMAIL USER_EMAIL, U.LOGIN USER_LOGIN,"
                + " U.NAME USER_NAME, U.BIRTHDATE USER_DATE, "
                + "F.ID FRIEND_ID, F.EMAIL FRIEND_EMAIL, F.LOGIN FRIEND_LOGIN,  "
                + "F.NAME FRIEND_NAME, F.BIRTHDATE FRIEND_DATE "
                + "FROM FRIENDSHIP FR JOIN USERS U ON FR.FRIEND = U.ID "
                + "LEFT JOIN FRIENDSHIP FRR ON U.ID = FRR.USER_ID AND FRR.ACCEPTED = TRUE "
                + "LEFT JOIN USERS F ON FRR.FRIEND = F.ID "
                + "WHERE FR.USER_ID = ? AND FR.ACCEPTED = TRUE";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, user.getId());
        return commonGetUserList(rs);
    }

    private String getSqlForUserList() {
        return "SELECT U.ID USER_ID, U.EMAIL USER_EMAIL, U.LOGIN USER_LOGIN, "
                + "U.NAME  USER_NAME, U.BIRTHDATE USER_DATE, "
                + "FR.ID FRIEND_ID, FR.EMAIL FRIEND_EMAIL, FR.LOGIN FRIEND_LOGIN, "
                + "FR.NAME  FRIEND_NAME, FR.BIRTHDATE FRIEND_DATE "
                + "FROM USERS U "
                + "LEFT JOIN FRIENDSHIP FS ON U.ID = FS.USER_ID AND FS.ACCEPTED = TRUE "
                + "LEFT JOIN USERS FR ON FS.FRIEND = FR.ID ";
    }

    private List<User> commonGetUserList(SqlRowSet rs) {
        Map<Integer, User> usersMap = new HashMap<>();
        while (rs.next()) {
            int userId;
            User currentUser;
            userId = rs.getInt("USER_ID");
            if (!usersMap.containsKey(userId)) {
                currentUser = makeUser(rs);
            } else {
                currentUser = usersMap.get(userId);
            }
            int friendId = rs.getInt("FRIEND_ID");
            if (friendId != 0) {
                currentUser.addFriend(friendId);
            }
            usersMap.put(userId, currentUser);
        }
        return new ArrayList<>(usersMap.values());
    }

    private User makeUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("USER_ID"));
        user.setEmail(rs.getString("USER_EMAIL"));
        user.setLogin(rs.getString("USER_LOGIN"));
        user.setName(rs.getString("USER_NAME"));
        user.setBirthday(rs.getDate("USER_DATE").toLocalDate());
        return user;
    }

    private User makeFriend(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("FRIEND_ID"));
        user.setEmail(rs.getString("FRIEND_EMAIL"));
        user.setLogin(rs.getString("FRIEND_LOGIN"));
        user.setName(rs.getString("FRIEND_NAME"));
        user.setBirthday(rs.getDate("FRIEND_DATE").toLocalDate());
        return user;
    }

}
