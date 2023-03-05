package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreDao {
    List<Genre> findAll();

    Genre findById(Integer id);

    Set<Genre> getGenresFilm(Long filmId);
}
