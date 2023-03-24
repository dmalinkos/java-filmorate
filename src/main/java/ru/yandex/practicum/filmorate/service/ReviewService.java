package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.Impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.Impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewDao reviewDao;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final EventDao eventDao;

    public Review add(@NotNull Review review) {
        userDbStorage.isExist(review.getUserId());
        filmDbStorage.isExist(review.getFilmId());
        Review addedReview = reviewDao.add(review);
        eventDao.create(Event.builder()
                .userId(addedReview.getUserId())
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .entityId(addedReview.getReviewId())
                .build());
        return addedReview;
    }

    public Review readByReviewId(Long id) {
        return reviewDao.readByReviewId(id);
    }

    public Review update(Review review) {
        Review updatedReview = reviewDao.update(review);
        eventDao.create(Event.builder()
                .userId(updatedReview.getUserId())
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .entityId(updatedReview.getReviewId())
                .build());
        return updatedReview;
    }

    public Review deleteReviewById(Long id) {
        Review deletedReview = reviewDao.deleteById(id);
        eventDao.create(Event.builder()
                .userId(deletedReview.getUserId())
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .entityId(deletedReview.getReviewId())
                .build());
        return deletedReview;
    }

    public Review setNewRateFromUser(Long userId, Long reviewId, Integer newRate) {
        return reviewDao.setNewRateOfUser(userId, reviewId, newRate);
    }

    public Review deleteRateFromUser(Long userId, Long reviewId) {
        return reviewDao.deleteRateFromUser(userId, reviewId);
    }

    public List<Review> getReviews(Long filmId, Integer count) {
        if (filmId == null) {
            log.info("filmId не указан. получение списка {} отзывов, отсортированных по рейтингу полезности", count);
            return getTopRatedReviews(count);
        }
        log.info("получение списка {} отзывов для filmId {}, отсортированных по рейтингу полезности", count, filmId);
        return getTopRatedReviewsByFilmId(filmId, count);
    }

    private List<Review> getTopRatedReviews(Integer count) {
        return reviewDao.getTopRatedReviews(count);
    }

    private List<Review> getTopRatedReviewsByFilmId(Long filmId, Integer count) {
        return reviewDao.getTopRatedReviewsByFilmId(filmId, count);
    }
}