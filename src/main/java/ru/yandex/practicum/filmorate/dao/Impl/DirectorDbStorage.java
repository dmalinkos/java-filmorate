package ru.yandex.practicum.filmorate.dao.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");

        director.setId(simpleJdbcInsert.executeAndReturnKey(directorToMap(director)).longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE directors SET director_name = ? WHERE director_id = ?";
        int result = jdbcTemplate.update(sqlQuery
                , director.getName()
                , director.getId());
        if (result > 0) {
            return director;
        } else {
            throw new EntityNotExistException(String.format("Режиссера с ID:%d нет в базе.", director.getId()));
        }
    }

    @Override
    public Director delete(Long directorId) {
        Director director = getDirector(directorId);
        String sqlQuery = "DELETE FROM directors WHERE director_id = ?";
        if (jdbcTemplate.update(sqlQuery, directorId) > 0) {
            return director;
        } else {
            throw new EntityNotExistException(String.format("Режиссера с ID:%d нет в базе.", directorId));
        }
    }

    @Override
    public Director getDirector(Long directorId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM directors WHERE director_id = ?",
                    this::mapRowToDirector, directorId);
        } catch (Exception e) {
            throw new EntityNotExistException(String.format("Режиссера с ID:%d нет в базе.", directorId));
        }
    }

    @Override
    public Collection<Director> getDirectors() {
        String sqlQuery = "SELECT * FROM directors";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Film updateFilmDirectors(Film film) {
        String sqlQueryDel = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryDel, film.getId());

        return addFilmDirectors(film);
    }

    @Override
    public Film addFilmDirectors(Film film) {
        if (film.getDirectors() == null) {
            return film;
        }

        Long filmId = film.getId();
        List<Director> directors = new ArrayList<>(film.getDirectors());
        String sqlQuery = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

        BatchPreparedStatementSetter batchPreparedStatementSetter = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Director director = directors.get(i);
                ps.setLong(1, filmId);
                ps.setLong(2, director.getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        };

        jdbcTemplate.batchUpdate(sqlQuery, batchPreparedStatementSetter);
        return film;
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }

    private Map<String, Object> directorToMap(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("director_name", director.getName());
        return values;
    }
}
