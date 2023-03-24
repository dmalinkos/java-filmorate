package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @DeleteMapping("/{id}")
    public Film delete(@PathVariable("id") Long filmId) {
        return filmService.delete(filmId);
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
    public ArrayList<Film> getMostPopular(@RequestParam(required = false, defaultValue = "10") Integer count,
                                          @RequestParam(required = false) Optional<Integer> genreId,
                                          @RequestParam(required = false) Optional<Integer> year) {
        return filmService.getMostPopular(count, genreId, year);
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

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable("directorId") Long directorId,
                                       @RequestParam(defaultValue = "name", required = false) String sortBy) {
        log.debug(String.format("Получен GET запрос к эндпоинту films/director на получение " +
                "всех фильмов режиссера с ID:%d.", directorId));
        return filmService.getDirectorFilms(directorId, sortBy.toLowerCase());
    }
    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.debug(String.format("Получен GET запрос к эндпоинту films/common на получение " +
                "общих фильмов пользователя с ID:%d. и его друга с ID:%d", userId, friendId));
        return filmService.getCommonFilms(userId, friendId);
    }
    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by){
        log.debug("Получен GET запрос к эндпоинту films/search на получение " +
                "фильмов по запросу " + query);
        return filmService.searchFilms(query, by);
    }
}
