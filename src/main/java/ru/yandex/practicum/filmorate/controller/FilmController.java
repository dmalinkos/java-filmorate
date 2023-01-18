package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.FilmNotExistException;
import ru.yandex.practicum.filmorate.exception.ReleaseDateNotPossible;
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
    private int id = 1;
    private final Map<Integer, Film> films = new HashMap<>();
    private final LocalDate begin = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(begin))
            throw new ReleaseDateNotPossible();
        log.info("Создание фильма ...");
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Фильм создан с id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Фильм с id={} не существует", film.getId());
            throw new FilmNotExistException();
        }
        log.info("Обновление фильма ...");
        films.put(film.getId(), film);
        log.info("Фильм с id={} обновлен", film.getId());
        return film;
    }

    @GetMapping
    public ArrayList<Film> findAll() {
        log.info("Получение списка всех фильмов ...");
        ArrayList<Film> allFilms = new ArrayList<>(films.values());
        log.info("Получен список всех фильмов");
        return allFilms;
    }

    private int generateId() {
        return id++;
    }
}
