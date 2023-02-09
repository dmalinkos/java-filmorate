package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    public void invalidLogin() {
        User user = User.builder()
                .login("")
                .email("user@ya.ru")
                .name("Name")
                .birthday(LocalDate.of(1998, 1, 1))
                .build();
        assertThrows(ConstraintViolationException.class,
                () -> userController.createUser(user)
        );
    }

    @Test
    public void invalidEmail() {
        User user = User.builder()
                .login("login")
                .email("user!ya.ru")
                .name("Name")
                .birthday(LocalDate.of(1998, 1, 1))
                .build();
        assertThrows(ConstraintViolationException.class,
                () -> userController.createUser(user)
        );
    }

    @Test
    public void emptyName() {
        User user = User.builder()
                .login("login")
                .email("user@ya.ru")
                .birthday(LocalDate.of(1998, 1, 1))
                .build();
        assertDoesNotThrow(() -> userController.createUser(user));
    }

    @Test
    public void invalidBirthday() {
        User user = User.builder()
                .login("login")
                .email("user@ya.ru")
                .name("Name")
                .birthday(LocalDate.of(2030, 1, 1))
                .build();
        assertThrows(ConstraintViolationException.class,
                () -> userController.createUser(user)
        );
    }
}
