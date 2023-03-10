package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User add(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public ArrayList<User> findAll() throws SQLException {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        return userStorage.findById(id);
    }

    public User addFriend(Long userId, Long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public User removeFriend(Long userId, Long friendId) {
        return userStorage.removeFriend(userId, friendId);
    }

    public ArrayList<User> getCommonFriends(Long userId, Long friendId) throws SQLException {
        HashSet<Long> userFriends = (HashSet<Long>) userStorage.getFriendsIds(userId);
        HashSet<Long> friendFriends = (HashSet<Long>) userStorage.getFriendsIds(friendId);
        HashSet<Long> commonFriends = new HashSet<>();

        for (Long userFriendId : userFriends) {
            if (friendFriends.contains(userFriendId)) {
                commonFriends.add(userFriendId);
            }
        }
        return (ArrayList<User>) userStorage.findAll().stream()
                .filter(v -> commonFriends.contains(v.getId()))
                .collect(Collectors.toList());
    }

    public ArrayList<User> getFriends(Long id) throws SQLException {
        Set<Long> friendsSet = userStorage.findById(id).getSetFriends();
        return (ArrayList<User>) userStorage.findAll().stream()
                .filter(v -> friendsSet.contains(v.getId()))
                .collect(Collectors.toList());
    }
}
