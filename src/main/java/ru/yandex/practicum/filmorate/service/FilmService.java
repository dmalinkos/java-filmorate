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
        return directorStorage.getFilmDirectors(addedFilm);
    }

    public Film update(Film film) {
        Film updatedFilm = filmStorage.update(film);
        film.setId(updatedFilm.getId());
        directorStorage.updateFilmDirectors(film);
        return directorStorage.getFilmDirectors(updatedFilm);
    }

    public Film delete(Long filmId) {
        return directorStorage.getFilmDirectors(filmStorage.delete(filmId));
    }

    public Film like(Long filmId, Long userId) {
        Film film = directorStorage.getFilmDirectors(filmStorage.like(filmId, userId));
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
        Film film = directorStorage.getFilmDirectors(filmStorage.unlike(filmId, userId));
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
        return new ArrayList<>(directorStorage.getFilmsDirectors(filmStorage.getMostPopular(n)));
    }

    public ArrayList<Film> findAll() {
        return new ArrayList<>(directorStorage.getFilmsDirectors(filmStorage.findAll()));
    }

    public Film findById(Long id) {
        return directorStorage.getFilmDirectors(filmStorage.findById(id));
    }

    public List<Film> getDirectorFilms(Long directorId, String sortBy) {
        directorStorage.getDirector(directorId);
        return directorStorage.getFilmsDirectors(filmStorage.getDirectorFilms(directorId, sortBy));
    }
}
