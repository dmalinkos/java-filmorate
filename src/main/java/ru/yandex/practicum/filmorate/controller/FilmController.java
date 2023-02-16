package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping
    public ArrayList<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable(required = false) Long id) {
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public ArrayList<Film> getMostPopular(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.getMostPopular(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film like(
            @PathVariable(name = "id") Long filmId,
            @PathVariable Long userId) {
        return filmService.like(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film unlike(
            @PathVariable Long filmId,
            @PathVariable Long userId) {
        return filmService.unlike(filmId, userId);
    }
}
