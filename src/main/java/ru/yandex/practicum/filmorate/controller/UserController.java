package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.ArrayList;

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
}
