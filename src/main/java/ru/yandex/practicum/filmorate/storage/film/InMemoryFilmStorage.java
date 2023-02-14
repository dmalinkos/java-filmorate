package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Long id;
    private final Map<Long, Film> films;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
        id = 1L;
        films = new HashMap<>();
    }

    @Override
    public Film add(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new EntityNotExistException(String.format("Фильм с id=%d не существует",film.getId()));
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Film film) {
        return films.remove(film.getId());
    }

    @Override
    public ArrayList<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new EntityNotExistException(String.format("Фильм с id=%d не существует", filmId));
        }
        return films.get(filmId);
    }

    @Override
    public ArrayList<Film> getMostPopular(int n) {
        return (ArrayList<Film>) films.values().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikesSet().size()))
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public Film like(Long filmId, Long userId) {
        Film film = findById(filmId);
        userStorage.findById(userId);
        film.getLikesSet().add(userId);
        return film;
    }

    @Override
    public Film unlike(Long filmId, Long userId) {
        Film film = findById(filmId);
        userStorage.findById(userId);
        film.getLikesSet().remove(userId);
        return film;
    }

    private Long generateId() {
        return id++;
    }

}
