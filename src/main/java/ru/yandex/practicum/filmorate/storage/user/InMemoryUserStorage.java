package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryUserStorage implements  UserStorage{
    private Long id;
    final private Map<Long, User> users;

    public InMemoryUserStorage() {
        users = new HashMap<>();
        id = 1L;
    }

    @Override
    public User add(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(User user) {
        return users.remove(user.getId());
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new EntityNotExistException(String.format("Пользователя с id=%d не существует", user.getId()));
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Set<Long> getFriendsIds(Long userId) {
        return findById(userId).getSetFriends();
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.getSetFriends().add(friendId);
        friend.getSetFriends().add(userId);
        return user;
    }

    @Override
    public User removeFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.getSetFriends().remove(friendId);
        friend.getSetFriends().remove(userId);
        return user;
    }

    @Override
    public ArrayList<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long id) {
        if (!users.containsKey(id)) {
            throw new EntityNotExistException(String.format("Пользователя с id=%d не существует", id));
        }
        return users.get(id);
    }

    private Long generateId() {
        return id++;
    }
}
