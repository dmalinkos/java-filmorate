package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Test
    public void invalidName() {
        Film film = Film.builder()
                .name("")
                .description("Description")
                .duration(200)
                .releaseDate(LocalDate.of(1900, 3, 25))
                .build();
        assertThrows(ConstraintViolationException.class,
                () -> filmController.createFilm(film)
        );
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
        assertThrows(ConstraintViolationException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    public void invalidReleaseDate() {
        Film film = Film.builder()
                .name("name")
                .description("Description")
                .duration(200)
                .releaseDate(LocalDate.of(100, 3, 25))
                .build();
        assertThrows(ConstraintViolationException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    public void invalidDuration() {
        Film film = Film.builder()
                .name("name")
                .description("Description")
                .duration(-10)
                .releaseDate(LocalDate.of(1900, 3, 25))
                .build();
        assertThrows(ConstraintViolationException.class,
                () -> filmController.createFilm(film)
        );
    }

}
