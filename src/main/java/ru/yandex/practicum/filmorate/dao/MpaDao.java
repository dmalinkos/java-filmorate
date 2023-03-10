package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDao {
    List<Mpa> findAll();

    Mpa findById(Integer id);

    Mpa mpaByFilmId(Long filmId);
}
