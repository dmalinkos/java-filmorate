package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

public interface UserStorage {

    User add(User user);

    User update(User user);

    User delete(Long userId);

    Set<Long> getFriendsIds(Long userId);

    User addFriend(Long userId, Long friendId);

    User removeFriend(Long userId, Long friendId);

    ArrayList<User> findAll() throws SQLException;

    User findById(Long id);

    void isExist(Long id);
}
