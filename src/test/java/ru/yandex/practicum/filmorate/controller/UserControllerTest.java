package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void invalidLogin() {
        User user = User.builder()
                .login("")
                .email("user@ya.ru")
                .name("Name")
                .birthday(LocalDate.of(1998, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void invalidEmail() {
        User user = User.builder()
                .login("login")
                .email("user!ya.ru")
                .name("Name")
                .birthday(LocalDate.of(1998, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void emptyName() {
        User user = User.builder()
                .login("login")
                .email("user@ya.ru")
                .birthday(LocalDate.of(1998, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void invalidBirthday() {
        User user = User.builder()
                .login("login")
                .email("user@ya.ru")
                .name("Name")
                .birthday(LocalDate.of(2030, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

}
