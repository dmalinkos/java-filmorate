package ru.yandex.practicum.filmorate.dao;


import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

public interface FilmStorage {

    Film add(Film film);

    Film update(Film film);

    Film delete(Long filmId);

    Film like(Long filmId, Long userId);

    Film unlike(Long filmId, Long userId);

    ArrayList<Film> findAll();

    Film findById(Long id);

    ArrayList<Film> getMostPopular(int n);

    List<Film> getDirectorFilms(Long directorId, String sortBy);
    ArrayList<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> searchFilms( String query,  String by);
}
