package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {

    Review add(Review review);

    Review readByReviewId(Long id);

    Review update(Review review);

    Review deleteById(Long id);

    Review setNewRateOfUser(Long userId, Long reviewId, Integer newRate);

    Review deleteRateFromUser(Long userId, Long reviewId);

    List<Review> getTopRatedReviews(Integer count);

    List<Review> getTopRatedReviewsByFilmId(Long filmId, Integer count);
}

