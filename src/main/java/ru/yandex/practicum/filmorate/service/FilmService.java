package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;

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
        return directorStorage.getFilmDirectors(filmStorage.like(filmId, userId));
    }

    public Film unlike(Long filmId, Long userId) {
        return directorStorage.getFilmDirectors(filmStorage.unlike(filmId, userId));
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
