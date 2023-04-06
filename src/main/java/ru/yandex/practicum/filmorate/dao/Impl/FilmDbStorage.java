package ru.yandex.practicum.filmorate.dao.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Director;
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
        String sql = "INSERT INTO films (film_name, film_description, film_releaseDate, film_duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
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
            film.getGenres().forEach(genre -> jdbcTemplate.update(sqlGenres, generatedId, genre.getId()));
        }
        return findById(generatedId);
    }

    @Override
    public Film update(Film film) {
        isExist(film.getId());
        String sql = "UPDATE films SET film_name = ?, film_description = ?, film_releaseDate = ?, " +
                "film_duration = ?, mpa_id = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        String sqlDelGenres = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDelGenres, film.getId());
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return findById(film.getId());
        }
        String sqlAddGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        film.getGenres().forEach(genre -> jdbcTemplate.update(sqlAddGenres, film.getId(), genre.getId()));
        return findById(film.getId());
    }

    @Override
    public Film delete(Long filmId) {
        Film film = findById(filmId);
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        if (jdbcTemplate.update(sqlQuery, filmId) > 0) {
            return film;
        } else {
            throw new EntityNotExistException(String.format("Фильма с ID:%d нет в базе.", filmId));
        }
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
    public List<Film> findAll() {
        return new ArrayList<>(jdbcTemplate.query("SELECT * FROM films", this::mapRowToFilm));
    }

    @Override
    public Film findById(Long id) {
        isExist(id);
        String sql = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
    }

    @Override
    public List<Film> getListFilms(List<Long> filmList) {
        String inSql = String.join(",", Collections.nCopies(filmList.size(), "?"));

        return jdbcTemplate.query(
                String.format("SELECT * FROM films WHERE film_id IN (%s)", inSql),
                filmList.toArray(),
                this::mapRowToFilm);
    }

    @Override
    public List<Film> getMostPopular(int n, Optional<Integer> genreId, Optional<Integer> year) {
        StringBuilder query = new StringBuilder();
        List<Object> params = new ArrayList<>();
        query.append("SELECT f.* ")
                .append("FROM films AS f ")
                .append("LEFT JOIN (SELECT film_id, COUNT(Distinct USER_ID) as rating ")
                .append("FROM likes GROUP BY film_id) as l ON f.FILM_ID = l.FILM_ID ");
        if (genreId.isPresent()) {
            query.append("LEFT JOIN FILM_GENRES AS fg on f.FILM_ID = fg.FILM_ID ");
        }
        boolean whereAdded = false;
        if (year.isPresent()) {
            query.append("WHERE EXTRACT(YEAR FROM f.film_releaseDate) = ? ");
            params.add(year.get());
            whereAdded = true;
        }
        if (genreId.isPresent()) {
            query.append(whereAdded ? "AND " : "WHERE ")
                    .append("fg.GENRE_ID = ? ");
            params.add(genreId.get().longValue());
        }
        query.append("ORDER BY l.rating DESC LIMIT ? ");
        params.add(n);
        return new ArrayList<>(jdbcTemplate.query(query.toString(), this::mapRowToFilm, params.toArray()));
    }

    @Override
    public List<Film> getDirectorFilms(Long directorId, String sortBy) {
        String sort;
        switch (sortBy) {
            case "year":
                sort = "ORDER BY film_releaseDate, f.film_name";
                break;
            case "likes":
                sort = "ORDER BY like_count DESC, f.film_name";
                break;
            default:
                sort = "ORDER BY f.film_name";
        }

        String sqlQuery = "SELECT f.*, COUNT(fl.film_id) AS like_count " +
                "FROM films AS f " +
                "LEFT JOIN likes AS fl ON f.film_id = fl.film_id " +
                "WHERE f.film_id IN(SELECT film_id FROM film_directors WHERE director_id = ?)" +
                "GROUP BY f.film_id " +
                sort;

        List<Film> result = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);

        if (result.size() != 0) {
            return result;
        } else {
            return new ArrayList<>();
        }
    }
    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        String sql ="SELECT f.* FROM films AS f " +
                "LEFT JOIN likes AS fl ON f.film_id = fl.film_id " +
                "WHERE fl.film_id IN (SELECT film_id FROM likes WHERE user_id = ?) " +
                "AND fl.film_id IN (SELECT film_id FROM likes WHERE user_id = ?) " +
                "GROUP BY fl.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC";
        return new ArrayList<>(jdbcTemplate.query(sql, this::mapRowToFilm,userId,friendId));
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String searchQuery = "%" + query + "%";
        String sqlQuery = "SELECT f.* " +
                "FROM films f " +
                "LEFT JOIN likes lf ON f.film_id = lf.film_id " +
                "LEFT JOIN film_directors fd on f.film_id = fd.film_id " +
                "LEFT JOIN directors d on d.director_id = fd.director_id " +
                "WHERE %s " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(lf.user_id) DESC";
        if(by.contains("director") && by.contains("title")){
            String sql = String.format(sqlQuery,"LOWER(f.film_name) LIKE LOWER(?) OR LOWER(d.director_name) LIKE LOWER(?)");
            return new ArrayList<>(jdbcTemplate.query(sql, this::mapRowToFilm,  searchQuery, searchQuery));
        } else if (by.contains("director")) {
            String sql = String.format(sqlQuery,"LOWER(d.director_name) LIKE LOWER(?)");
            return new ArrayList<>(jdbcTemplate.query(sql, this::mapRowToFilm,  searchQuery));
        } else if (by.contains("title")) {
            String sql = String.format(sqlQuery,"LOWER(f.film_name) LIKE LOWER(?)");
            return new ArrayList<>(jdbcTemplate.query(sql, this::mapRowToFilm,  searchQuery));
        }
        return List.of();
    }

    @Override
    public Film getFilmDirectors(Film film) {
        String sqlQuery = "SELECT director_id, director_name FROM directors WHERE director_id IN(" +
                "SELECT director_id FROM film_directors WHERE film_id = ?)";
        film.setDirectors(new ArrayList<>(jdbcTemplate.query(sqlQuery, this::mapRowToDirector, film.getId())));
        return film;
    }

    @Override
    public List<Film> getFilmsDirectors(List<Film> films) {
        if (films.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, List<Director>> idToFilm = new LinkedHashMap<>();
        for (Film film : films) {
            idToFilm.put(film.getId(), film.getDirectors());
        }

        SqlParameterSource param = new MapSqlParameterSource("filmsId", idToFilm.keySet());
        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        String sqlQuery = "SELECT * FROM film_directors AS f " +
                "INNER JOIN directors AS dir ON dir.director_id = f.director_id " +
                "WHERE film_id IN(:filmsId)";

        namedJdbcTemplate.query(sqlQuery, param,
                (resultSet, rowNum) -> idToFilm.get(resultSet.getLong("film_id"))
                        .add(mapRowToDirector(resultSet, rowNum)));

        return new ArrayList<>(films);
    }

    public void isExist(Long id) {
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
                .directors(new ArrayList<>())
                .build();
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("director_id"))
                .name(resultSet.getString("director_name"))
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
