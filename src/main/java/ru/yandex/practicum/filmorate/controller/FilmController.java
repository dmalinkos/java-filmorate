package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int id;
    private final Map<Integer, Film> films;
    private static final LocalDate BEGIN = LocalDate.of(1895, 12, 28);
    public FilmController() {
        id = 1;
        films = new HashMap<>();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(BEGIN)) {
            log.warn("Дата релиза фильма не может быть раньше {}", BEGIN);
            throw new EntityNotExistException("Невозможная дата релиза фильма", FilmController.class.getName());
        }
        log.info("Создание фильма ...");
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Фильм создан с id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!isAfterBegin(film)) {
            log.warn("Фильм с id={} не существует", film.getId());
            throw new EntityNotExistException("Фильм с таким id не существует", FilmController.class.getName());
        }
        log.info("Обновление фильма ...");
        films.put(film.getId(), film);
        log.info("Фильм с id={} обновлен", film.getId());
        return film;
    }

    @GetMapping
    public ArrayList<Film> findAll() {
        log.info("Получение списка всех фильмов");
        return new ArrayList<>(films.values());
    }

    private int generateId() {
        return id++;
    }

    private boolean isAfterBegin(Film film) {
        return film.getReleaseDate().isBefore(BEGIN);
    }
}
