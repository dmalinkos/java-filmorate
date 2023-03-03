package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Set;

public interface UserStorage {

    User add(User user);

    User delete(User user);

    User update(User user);

    Set<Long> getFriendsIds(Long userId);

    User addFriend(Long userId, Long friendId);

    User removeFriend(Long userId, Long friendId);

    ArrayList<User> findAll();

    User findById(Long id);
}
