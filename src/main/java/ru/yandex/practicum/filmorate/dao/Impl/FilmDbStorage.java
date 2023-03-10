package ru.yandex.practicum.filmorate.dao.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final UserStorage userStorage;
    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO films (film_name, film_description, film_releaseDate, film_duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql,
                    new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        if (film.getGenres() != null) {
            String sqlGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
            film.getGenres().forEach(genre -> jdbcTemplate.update(sqlGenres,generatedId,genre.getId()));
        }
        return findById(generatedId);
    }

    @Override
    public Film update(Film film) {
        isExist(film.getId());
        String sql = "UPDATE films SET film_name = ?, film_description = ?, film_releaseDate = ?, film_duration = ?, mpa_id = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() == null) {
            return findById(film.getId());
        }
        String sqlDelGenres = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDelGenres, film.getId());
        if (film.getGenres().isEmpty()) {
            return findById(film.getId());
        }
        String sqlAddGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        film.getGenres().forEach(genre -> jdbcTemplate.update(sqlAddGenres, film.getId(), genre.getId()));
        return findById(film.getId());
    }

    @Override
    public Film like(Long filmId, Long userId) {
        userStorage.isExist(userId);
        Film film = findById(filmId);
        String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, userId, filmId);
        return film;
    }

    @Override
    public Film unlike(Long filmId, Long userId) {
        userStorage.isExist(userId);
        Film film = findById(filmId);
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
        return film;
    }

    @Override
    public ArrayList<Film> findAll() {
        return (ArrayList<Film>) jdbcTemplate.query("SELECT * FROM films", this::mapRowToFilm);
    }

    @Override
    public Film findById(Long id) {
        isExist(id);
        String sql = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
    }

    @Override
    public ArrayList<Film> getMostPopular(int n) {
        String sql = "SELECT f.* " +
                "FROM likes AS l " +
                "RIGHT JOIN FILMS f on f.FILM_ID = L.FILM_ID " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC "  +
                "LIMIT ?";
        return (ArrayList<Film>) jdbcTemplate.query(sql, this::mapRowToFilm, n);
    }

    private void isExist(Long id) {
        String sql = "SELECT FILM_ID FROM FILMS WHERE FILM_ID = ?";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sql, id);
        if (!filmRow.next()) {
            throw new EntityNotExistException(String.format("Фильм с id=%d не существует", id));
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("FILM_ID");
        String name = rs.getString("FILM_NAME");
        String description = rs.getString("FILM_DESCRIPTION");
        LocalDate releaseDate = rs.getDate("FILM_RELEASEDATE").toLocalDate();
        long duration = rs.getLong("FILM_DURATION");
        Set<Long> likes = getFilmLikes(id);

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpaDao.mpaByFilmId(id))
                .genres(genreDao.getGenresFilm(id))
                .likesSet(likes)
                .build();
    }

    private Set<Long> getFilmLikes(Long filmId) {
        Set<Long> likes = new HashSet<>();
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        SqlRowSet likesRow = jdbcTemplate.queryForRowSet(sql, filmId);
        while (likesRow.next()) {
            likes.add(likesRow.getLong("user_id"));
        }
        return likes;
    }
}
