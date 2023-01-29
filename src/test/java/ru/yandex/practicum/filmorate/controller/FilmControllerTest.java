package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class FilmControllerTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void invalidName() {
        Film film = Film.builder()
                .name("")
                .description("Description")
                .duration(200)
                .releaseDate(LocalDate.of(1900, 3, 25))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void invalidDescription() {
        String badDescription = "1".repeat(201);

        Film film = Film.builder()
                .name("name")
                .description(badDescription)
                .duration(120)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void invalidReleaseDate() {
        Film film = Film.builder()
                .name("name")
                .description("Description")
                .duration(200)
                .releaseDate(LocalDate.of(100, 3, 25))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void invalidDuration() {
        Film film = Film.builder()
                .name("name")
                .description("Description")
                .duration(-10)
                .releaseDate(LocalDate.of(1900, 3, 25))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

}
