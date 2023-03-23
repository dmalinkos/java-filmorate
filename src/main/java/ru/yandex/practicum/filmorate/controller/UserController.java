package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public User delete(@PathVariable("id") Long userId) {
        return userService.delete(userId);
    }

    @GetMapping
    public ArrayList<User> findAll() throws SQLException {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable(required = false) Long id) {
        return userService.findById(id);
    }

    @GetMapping("/{id}/friends")
    public ArrayList<User> getFriends(@PathVariable Long id) throws SQLException {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ArrayList<User> getCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) throws SQLException {
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(
            @PathVariable(value = "id") Long userId,
            @PathVariable Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(
            @PathVariable(value = "id") Long userId,
            @PathVariable Long friendId) {
        return userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
        log.info("GET-запрос /recommendations (получение рекомендаций). userId-id: {}", id);
        return userService.getRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public ArrayList<Event> getFeed(@PathVariable Long id) {
        return new ArrayList<>(userService.getFeed(id));
    }
}
