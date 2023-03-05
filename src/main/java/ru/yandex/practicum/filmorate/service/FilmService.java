package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film like(Long filmId, Long userId) {
        return filmStorage.like(filmId, userId);
    }

    public Film unlike(Long filmId, Long userId) {
        return filmStorage.unlike(filmId, userId);
    }

    public ArrayList<Film> getMostPopular(int n) {
        return filmStorage.getMostPopular(n);
    }

    public ArrayList<Film> findAll() {
        return new ArrayList<>(filmStorage.findAll());
    }

    public Film findById(Long id) {
        return filmStorage.findById(id);
    }
}
