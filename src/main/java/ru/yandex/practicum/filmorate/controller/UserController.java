package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.UserNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя ...");
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Пользователь создан с id={}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Пользователя с id={} не существует", user.getId());
            throw new UserNotExistException();
        }
        log.info("Обновление пользователя ...");
        users.put(user.getId(), user);
        log.info("Пользователь с id={} обновлен", user.getId());
        return user;
    }

    @GetMapping
    public ArrayList<User> findAll() {
        log.info("Получение списка всех пользователей ...");
        ArrayList<User> allUsers = new ArrayList<>(users.values());
        log.info("Получен список всех пользователей");
        return allUsers;
    }

    private int generateId() {
        return id++;
    }
}



