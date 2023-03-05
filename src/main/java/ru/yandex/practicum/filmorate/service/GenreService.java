package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDao genreDao;

    public ArrayList<Genre> findAll() {
        return (ArrayList<Genre>) genreDao.findAll();
    }

    public Genre findById(Integer id) {
        return genreDao.findById(id);
    }
}
