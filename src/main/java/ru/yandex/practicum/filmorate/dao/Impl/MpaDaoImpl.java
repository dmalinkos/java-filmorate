package ru.yandex.practicum.filmorate.dao.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public ArrayList<Mpa> findAll() {
        return (ArrayList<Mpa>) jdbcTemplate.query("SELECT * FROM MPA", this::mapRowToMpa);
    }

    @Override
    public Mpa findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM MPA WHERE mpa_id = ?", this::mapRowToMpa, id)
                .stream()
                .findFirst().orElseThrow(() -> new EntityNotExistException(String.format("MPA с id=%d не сущесвует",id)));
    }

    @Override
    public Mpa mpaByFilmId(Long filmId) {
        String sql = "SELECT f.mpa_id, m.mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN MPA AS m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, filmId);
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("MPA_ID"))
                .name(rs.getString("MPA_NAME"))
                .build();
    }
}
