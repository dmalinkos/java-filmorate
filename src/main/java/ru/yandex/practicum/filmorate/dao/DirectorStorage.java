package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface DirectorStorage {
    Director create(Director director);

    Director update(Director director);

    Director delete(Long directorId);

    Director getDirector(Long directorId);

    Collection<Director> getDirectors();

    Film getFilmDirectors(Film film);

    List<Film> getFilmsDirectors(List<Film> films);

    Film updateFilmDirectors(Film film);

    Film addFilmDirectors(Film film);
}
