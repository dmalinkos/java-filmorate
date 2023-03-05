package ru.yandex.practicum.filmorate.dao.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        String sql = "INSERT INTO USERS (USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql,
                    new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(generatedId);
        return user;
    }

    @Override
    public User update(User user) {
        isExist(user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        String sql = "UPDATE USERS SET USER_EMAIL = ?, USER_LOGIN = ?, USER_NAME = ?, USER_BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Set<Long> getFriendsIds(Long userId) {
        Set<Long> friends = new HashSet<>();
        String sql = "SELECT FRIEND_ID FROM FRIENDSHIPS WHERE USER_ID = ?";

        SqlRowSet friendsSetRow = jdbcTemplate.queryForRowSet(sql, userId);
        while (friendsSetRow.next()) {
            friends.add(friendsSetRow.getLong("FRIEND_ID"));
        }
        return friends;
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        isExist(userId);
        isExist(friendId);
        String sql = "INSERT INTO FRIENDSHIPS (USER_ID, FRIEND_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
        return findById(userId);
    }

    @Override
    public User removeFriend(Long userId, Long friendId) {
        isExist(userId);
        isExist(friendId);
        String sql = "DELETE FROM friendships WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, userId, friendId);
        return findById(userId);
    }

    @Override
    public ArrayList<User> findAll() {
        return (ArrayList<User>) jdbcTemplate.query("SELECT * FROM users", this::mapRowToUser);
    }

    @Override
    public User findById(Long id) {
        isExist(id);
        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
    }

    @Override
    public void isExist(Long id) {
        String sql = "SELECT USER_ID FROM USERS WHERE USER_ID = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sql, id);
        if (!userRow.next()) {
            throw new EntityNotExistException(String.format("Пользователя с id=%d не существует", id));
        }
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("USER_ID");
        String email = rs.getString("USER_EMAIL");
        String login = rs.getString("USER_LOGIN");
        String name = rs.getString("USER_NAME");
        LocalDate birthday = Objects.requireNonNull(rs.getDate("USER_BIRTHDAY")).toLocalDate();
        Set<Long> friends = getFriendsIds(id);
        return User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .setFriends(friends)
                .build();
    }
}
