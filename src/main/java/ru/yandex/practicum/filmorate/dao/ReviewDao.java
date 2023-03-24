package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ReviewDao {

    Review add(@NotNull Review review);

    Review readByReviewId(@NotNull Long id);

    Review update(@NotNull Review review);

    Review deleteById(@NotNull Long id);

    Review setNewRateOfUser(@NotNull Long userId, @NotNull Long reviewId, @NotNull Integer newRate);

    Review deleteRateFromUser(@NotNull Long userId, @NotNull Long reviewId);

    List<Review> getTopRatedReviews(@NotNull Integer count);

    List<Review> getTopRatedReviewsByFilmId(@NotNull Long filmId, @NotNull Integer count);
}