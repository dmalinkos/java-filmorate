package ru.yandex.practicum.filmorate.dao.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT * FROM genres", this::mapRowToGenre);
    }

    @Override
    public Genre findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM genres WHERE genre_id = ?", this::mapRowToGenre, id)
                .stream()
                .findFirst().orElseThrow(() -> new EntityNotExistException(String.format("Фильма с id=%d не существует", id)));
    }

    @Override
    public Set<Genre> getGenresFilm(Long filmId) {
        String sql = "SELECT fg.genre_id,  g.genre_name " +
                "FROM film_genres AS fg " +
                "LEFT JOIN films AS f ON fg.film_id = f.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, this::mapRowToGenre, filmId));
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("GENRE_ID"))
                .name(rs.getString("GENRE_NAME"))
                .build();
    }
}
