package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film add(Film film);

    Film update(Film film);

    Film delete(Long filmId);

    Film like(Long filmId, Long userId);

    Film unlike(Long filmId, Long userId);

    ArrayList<Film> findAll();

    Film findById(Long id);

    ArrayList<Film> getMostPopular(int n, Optional<Integer> genreId, Optional<Integer> year);

    List<Film> getDirectorFilms(Long directorId, String sortBy);

    ArrayList<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> searchFilms(String query, String by);

    List<Film> getListFilms(List<Long> filmList);

    Film getFilmDirectors(Film film);

    List<Film> getFilmsDirectors(List<Film> films);
}
