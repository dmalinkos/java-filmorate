package ru.yandex.practicum.filmorate.dao.Impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Review;

import javax.validation.ValidationException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewDBStorageTest {

    private final ReviewDBStorage reviewDBStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void reinitialiseDB() {
        jdbcTemplate.update("DELETE FROM review_rates;");
        jdbcTemplate.update("DELETE FROM reviews;");
        jdbcTemplate.update("ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1");
    }

    @Test
    void add() {
        Review initial = Review.builder()
                .content("Genius")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();

        Review actual = reviewDBStorage.add(initial);

        assertThat(actual).isInstanceOf(Review.class).hasFieldOrPropertyWithValue("reviewId", 1L);
    }

    @Test
    void callMethodsWithNullParameters() {
        assertThrows(ValidationException.class , () -> reviewDBStorage.add(null));
        assertThrows(ValidationException.class , () -> reviewDBStorage.readByReviewId(null));
        assertThrows(ValidationException.class , () -> reviewDBStorage.update(null));
        assertThrows(ValidationException.class , () -> reviewDBStorage.deleteById(null));
        assertThrows(ValidationException.class , () -> reviewDBStorage.setNewRateOfUser(null, 1L, 1));
        assertThrows(ValidationException.class , () -> reviewDBStorage.deleteRateFromUser(null, 1L));
        assertThrows(ValidationException.class , () -> reviewDBStorage.getTopRatedReviewsByFilmId(1L, null));
        assertThrows(ValidationException.class , () -> reviewDBStorage.getTopRatedReviews(null));
    }

    @Test
    void readByReviewId() {
        Review dummy = Review.builder().content("").isPositive(true).userId(1L).filmId(1L).build();
        Review initial = Review.builder()
                .content("Bad")
                .isPositive(false)
                .userId(2L)
                .filmId(2L)
                .build();

        reviewDBStorage.add(dummy);
        Long id2 = reviewDBStorage.add(initial).getReviewId();
        Review actual = reviewDBStorage.readByReviewId(id2);

        assertThat(actual)
                .hasFieldOrPropertyWithValue("reviewId", 2L)
                .hasFieldOrPropertyWithValue("content", "Bad")
                .hasFieldOrPropertyWithValue("isPositive", false)
                .hasFieldOrPropertyWithValue("userId", 2L)
                .hasFieldOrPropertyWithValue("filmId", 2L)
                .hasFieldOrPropertyWithValue("useful", 0L);
    }

    @Test
    void update() {
        Review initial = Review.builder().content("").isPositive(true).userId(1L).filmId(1L).build();
        Long id = reviewDBStorage.add(initial).getReviewId();
        Review updated = Review.builder()
                .reviewId(1L)
                .content("Bad")
                .isPositive(false)
                .userId(1L)
                .filmId(1L)
                .build();
        Review withBadId = Review.builder().reviewId(2L).content("").isPositive(true).userId(1L).filmId(1L).build();

        reviewDBStorage.update(updated);

        assertThat(reviewDBStorage.readByReviewId(id))
                .hasFieldOrPropertyWithValue("reviewId", 1L)
                .hasFieldOrPropertyWithValue("content", "Bad")
                .hasFieldOrPropertyWithValue("isPositive", false)
                .hasFieldOrPropertyWithValue("userId", 1L)
                .hasFieldOrPropertyWithValue("filmId", 1L)
                .hasFieldOrPropertyWithValue("useful", 0L);
        assertThrows(EntityNotExistException.class, () -> reviewDBStorage.update(withBadId));
    }

    @Test
    void deleteById() {
        Review initial = Review.builder()
                .content("Bad")
                .isPositive(false)
                .userId(2L)
                .filmId(2L)
                .build();
        Long badId = 999L;

        Long id = reviewDBStorage.add(initial).getReviewId();

        assertEquals(initial, reviewDBStorage.deleteById(id));
        assertThrows(EntityNotExistException.class, () -> reviewDBStorage.deleteById(badId));
    }

    @Test
    void setNewRateOfUser() {
        Review initial = Review.builder()
                .content("Genius")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();

        Long id = reviewDBStorage.add(initial).getReviewId();
        reviewDBStorage.setNewRateOfUser(2L, id, 1);

        assertThat(reviewDBStorage.readByReviewId(id))
                .hasFieldOrPropertyWithValue("useful", 1L);
    }

    @Test
    void deleteRateOfUser() {
        Long userId = 2L;
        Review dummy = Review.builder()
                .content("Genius")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();

        Long reviewId = reviewDBStorage.add(dummy).getReviewId();
        reviewDBStorage.setNewRateOfUser(userId, reviewId, 1);
        String sqlSelectQuery = "SELECT rate_value FROM review_rates WHERE rated_by_id = ? AND review_id = ?;";
        int value = jdbcTemplate.queryForObject(sqlSelectQuery,
                (rs, rowNum) -> rs.getInt("rate_value"),
                userId,
                reviewId);
        Review initial = reviewDBStorage.readByReviewId(reviewId);
        Review actual = reviewDBStorage.deleteRateFromUser(userId, reviewId);

        assertEquals(1, value);
        assertThat(initial)
                .hasFieldOrPropertyWithValue("useful", 1L);
        assertThat(actual)
                .hasFieldOrPropertyWithValue("useful", 0L);
    }

    @Test
    void getTopRatedReviews() {
        Review review1 = Review.builder()
                .content("Genius")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();
        Review review2 = Review.builder()
                .content("Bad")
                .isPositive(false)
                .userId(2L)
                .filmId(2L)
                .build();
        Long userLikedReview1Id = 2L;

        Long reviewId1 = reviewDBStorage.add(review1).getReviewId();
        reviewDBStorage.add(review2).getReviewId();
        reviewDBStorage.setNewRateOfUser(userLikedReview1Id, reviewId1, 1);
        List<Review> topRatedReviews = reviewDBStorage.getTopRatedReviews(10);

        assertEquals(2, topRatedReviews.size());
        assertThat(topRatedReviews.get(0))
                .hasFieldOrPropertyWithValue("content", "Genius")
                .hasFieldOrPropertyWithValue("useful", 1L);
        assertThat(topRatedReviews.get(1))
                .hasFieldOrPropertyWithValue("content", "Bad")
                .hasFieldOrPropertyWithValue("useful", 0L);
    }

    @Test
    void getTopRatedReviewsByFilmId() {
        Review review1 = Review.builder()
                .content("Genius")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();
        Review review2 = Review.builder()
                .content("Bad")
                .isPositive(false)
                .userId(2L)
                .filmId(1L)
                .build();
        Long user1LikedReview1Id = 2L;
        Long user2LikedReview1Id = 3L;
        Long user3LikedReview1Id = 4L;

        Long reviewId1 = reviewDBStorage.add(review1).getReviewId();
        reviewDBStorage.add(review2);
        reviewDBStorage.setNewRateOfUser(user1LikedReview1Id, reviewId1, 1);
        reviewDBStorage.setNewRateOfUser(user2LikedReview1Id, reviewId1, 1);
        reviewDBStorage.setNewRateOfUser(user3LikedReview1Id, reviewId1, -1);
        List<Review> topRatedReviewsByFilmId = reviewDBStorage.getTopRatedReviewsByFilmId(1L, 10);

        assertEquals(2, topRatedReviewsByFilmId.size());
        assertThat(topRatedReviewsByFilmId.get(0))
                .hasFieldOrPropertyWithValue("content", "Genius")
                .hasFieldOrPropertyWithValue("useful", 1L);
        assertThat(topRatedReviewsByFilmId.get(1))
                .hasFieldOrPropertyWithValue("content", "Bad")
                .hasFieldOrPropertyWithValue("useful", 0L);
    }
}