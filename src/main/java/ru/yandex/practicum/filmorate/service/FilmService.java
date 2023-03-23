package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.dao.FilmStorage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final EventDao eventDao;

    public Film add(Film film) {
        Film addedFilm = filmStorage.add(film);
        film.setId(addedFilm.getId());
        directorStorage.addFilmDirectors(film);
        return filmStorage.getFilmDirectors(addedFilm);
    }

    public Film update(Film film) {
        Film updatedFilm = filmStorage.update(film);
        film.setId(updatedFilm.getId());
        directorStorage.updateFilmDirectors(film);
        return filmStorage.getFilmDirectors(updatedFilm);
    }

    public Film delete(Long filmId) {
        return filmStorage.getFilmDirectors(filmStorage.delete(filmId));
    }

    public Film like(Long filmId, Long userId) {
        Film film = filmStorage.getFilmDirectors(filmStorage.like(filmId, userId));
        eventDao.create(Event.builder()
                .userId(userId)
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(filmId)
                .build());
        return film;
    }

    public Film unlike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmDirectors(filmStorage.unlike(filmId, userId));
        eventDao.create(Event.builder()
                .userId(userId)
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .entityId(filmId)
                .build());
        return film;
    }

    public ArrayList<Film> getMostPopular(int n) {
        return new ArrayList<>(filmStorage.getFilmsDirectors(filmStorage.getMostPopular(n)));
    }

    public ArrayList<Film> findAll() {
        return new ArrayList<>(filmStorage.getFilmsDirectors(filmStorage.findAll()));
    }

    public Film findById(Long id) {
        return filmStorage.getFilmDirectors(filmStorage.findById(id));
    }

    public List<Film> getDirectorFilms(Long directorId, String sortBy) {
        directorStorage.getDirector(directorId);
        return filmStorage.getFilmsDirectors(filmStorage.getDirectorFilms(directorId, sortBy));
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getFilmsDirectors(filmStorage.getCommonFilms(userId, friendId));
    }

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.getFilmsDirectors(filmStorage.searchFilms(query, by));
    }
}
