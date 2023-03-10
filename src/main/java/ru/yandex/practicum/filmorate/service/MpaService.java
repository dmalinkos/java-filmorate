package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDao mpaDao;

    public ArrayList<Mpa> findAll() {
        return (ArrayList<Mpa>) mpaDao.findAll();
    }

    public Mpa findById(Integer id) {
        return mpaDao.findById(id);
    }
}
