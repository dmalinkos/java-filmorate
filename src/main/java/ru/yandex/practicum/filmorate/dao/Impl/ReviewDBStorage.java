package ru.yandex.practicum.filmorate.dao.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class ReviewDBStorage implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review add(Review review) {
        log.info("Обработка SQL-запроса создания отзыва. Пользователь: {}; фильм: {}",
                                                                        review.getUserId(), review.getFilmId());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                                                .withTableName("reviews")
                                                .usingGeneratedKeyColumns("review_id");
        try {
            Long newId = simpleJdbcInsert.executeAndReturnKey(reviewToMap(review)).longValue();
            log.info("В БД добавлена запись. id {}", newId);
            review.setReviewId(newId);
            return review;
        } catch (DataIntegrityViolationException dive) {
            throw new EntityNotExistException(dive.getMessage());
        }
    }

    @Override
    public Review readByReviewId(Long id) {
        String sqlQuery = "SELECT r.*, SUM(rl.like_value) AS useful "
                        + "FROM reviews AS r "
                        + "LEFT OUTER JOIN review_likes AS rl ON rl.review_id = r.review_id "
                        + "WHERE r.review_id = ? "
                        + "GROUP BY r.review_id;";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException erdta) {
            throw new EntityNotExistException(String.format("Отзыв с id=%d не существует", id));
        } catch (DataAccessException dae) {
            throw new RuntimeException("Ошибка доступа к БД (DataAccessException)");
        }
    }

    @Override
    public Review update(Review review) {
        log.info("Обработка SQL-запроса обновления отзыва. Id: {}", review.getReviewId());
        String sqlQuery = "UPDATE reviews "
                        + "SET content = ?, user_id = ?, is_positive = ?, film_id = ?, is_liked = ? "
                        + "WHERE review_id = ?;";
        try {
            int affectedRows = jdbcTemplate.update(sqlQuery
                                                    , review.getContent()
                                                    , review.getUserId()
                                                    , review.getIsPositive()
                                                    , review.getFilmId()
                                                    // TODO delete
                                                    , review.getIsLiked()
                                                    , review.getReviewId());
            if (affectedRows == 1) {
                log.info("отзыв-id {} обновлен", review.getReviewId());
                return review;
            }
            if (affectedRows == 0) {
                throw new EntityNotExistException(String.format("Отзыв с id=%d не существует", review.getReviewId()));
            }
            throw new RuntimeException(String.format("Ошибка обновления данных в БД для отзыва id %d", review.getReviewId()));
        } catch (DataIntegrityViolationException dive) {
            throw new EntityNotExistException(dive.getMessage());
        } catch (DataAccessException dae) {
            throw new RuntimeException("Ошибка доступа к БД (DataAccessException)");
        }
    }

    @Override
    public Review deleteById(Long id) {
        log.info("Обработка SQL-запроса удаления отзыва. Id: {}", id);
        Review review;
        String sqlQuery = "DELETE FROM reviews WHERE review_id = ?;";
        try {
            review = readByReviewId(id);
            int affectedRows = jdbcTemplate.update(sqlQuery, id);
            if (affectedRows == 1) {
                log.info("Отзыв-id {} удален", id);
                return review;
            }
            throw new RuntimeException(String.format("Ошибка удаления из БД для отзыва id %d", review.getReviewId()));
        } catch (DataAccessException dae) {
            throw new RuntimeException("Ошибка доступа к БД при удалении отзыва из БД (DataAccessException)");
        }
    }

    @Override
    public Review setNewRateOfUser(Long userId, Long reviewId, Integer newRate) {
        log.info("Обработка SQL-запроса добавления оценки {} отзыва-Id: {} пользователем-id: {}"
                , newRate, reviewId, userId);
        try {
            String sqlDeleteQuery = "DELETE FROM review_likes WHERE user_id = ? AND review_id = ?;";
            int affectedReviewLikesRows = jdbcTemplate.update(sqlDeleteQuery , userId, reviewId);
            if (affectedReviewLikesRows == 1) {
                log.info("при добавлении оценки отзыва в БД обновляется существующая оценка");
            }
            String sqlInsertQuery = "INSERT INTO review_likes (review_id, user_id, like_value) "
                                  + "VALUES (?, ?, ?);";
            affectedReviewLikesRows = jdbcTemplate.update(sqlInsertQuery, reviewId, userId, newRate);
            if (affectedReviewLikesRows == 1) {
                log.info("Оценка отзыва добавлена в БД");
                return readByReviewId(reviewId);
            }
            throw new RuntimeException(String.format("Ошибка добавления оценки отзыва в БД"));
        } catch (DataIntegrityViolationException dive) {
            throw new EntityNotExistException(dive.getMessage());
        } catch (DataAccessException dae) {
            throw new RuntimeException("Ошибка доступа к БД при добавлении оценки отзыва (DataAccessException)");
        }
    }

    @Override
    public Review deleteRateOfUser(Long userId, Long reviewId) {
        log.info("Обработка SQL-запроса удаления оценки отзыва-Id: {} пользователем-id: {}", userId, reviewId);
        try {
            String sqlDeleteQuery = "DELETE FROM review_likes WHERE user_id = ? AND review_id = ?;";
            int affectedReviewLikesRows = jdbcTemplate.update(sqlDeleteQuery , userId, reviewId);
            if (affectedReviewLikesRows == 1) {
                log.info("удалена оценка отзыва в БД");
                return readByReviewId(reviewId);
            }
            throw new RuntimeException(String.format("Ошибка удаления оценки отзыва из БД"));
        } catch (DataIntegrityViolationException dive) {
            throw new EntityNotExistException(dive.getMessage());
        } catch (DataAccessException dae) {
            throw new RuntimeException("Ошибка доступа к БД при удалении оценки отзыва (DataAccessException)");
        }
    }

    @Override
    public List<Review> getTopRatedReviews(Integer count) {
        log.info("Обработка SQL-запроса получения списка {} отзывов, отсортированных по рейтингу полезности"
                , count);
        String sqlQuery = "SELECT r.*, SUM(rl.like_value) AS useful "
                + "FROM reviews AS r "
                + "LEFT OUTER JOIN review_likes AS rl ON rl.review_id = r.review_id "
                + "GROUP BY r.review_id "
                + "ORDER BY SUM(rl.like_value) DESC "
                + "LIMIT ?;";
        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
        } catch (DataAccessException dae) {
            throw new RuntimeException("Ошибка доступа к БД при получении списка отзывов (DataAccessException)");
        }
    }

    @Override
    public List<Review> getTopRatedReviewsByFilmId(Long filmId, Integer count) {
        log.info("Обработка SQL-запроса получения списка {} отзывов для filmId {}, отсортированных по рейтингу полезности"
                , count, filmId);
        String sqlQuery = "SELECT r.*, SUM(rl.like_value) AS useful "
                        + "FROM reviews AS r "
                        + "LEFT OUTER JOIN review_likes AS rl ON rl.review_id = r.review_id "
                        + "WHERE r.film_id = ? "
                        + "GROUP BY r.review_id "
                        + "ORDER BY SUM(rl.like_value) DESC "
                        + "LIMIT ?;";
        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
        } catch (DataAccessException dae) {
            throw new RuntimeException("Ошибка доступа к БД при получении списка отзывов (DataAccessException)");
        }
    }

    private Map<String, Object> reviewToMap(Review review) {
        Map<String, Object> mapReview = new HashMap<>();
        mapReview.put("content", review.getContent());
        mapReview.put("user_id", review.getUserId());
        mapReview.put("is_positive", review.getIsPositive());
        mapReview.put("film_id", review.getFilmId());
        //TODO delete
        mapReview.put("is_liked", review.getIsLiked());
        mapReview.put("useful", review.getUseful());
        return mapReview;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .useful(resultSet.getLong("useful"))
                .build();
    }
}
