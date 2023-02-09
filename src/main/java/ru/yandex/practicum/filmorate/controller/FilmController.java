package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController {
    private int id;
    private final Map<Integer, Film> films;
    public FilmController() {
        id = 1;
        films = new HashMap<>();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Создание фильма ...");
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Фильм создан с id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с id={} не существует", film.getId());
            throw new EntityNotExistException("Фильм с таким id не существует", Film.class.getName());
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

}
