package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.Impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.Impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewDao reviewDao;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    public Review add(Review review) {
        userDbStorage.isExist(review.getUserId());
        filmDbStorage.isExist(review.getFilmId());
        return reviewDao.add(review);
    }

    public Review readByReviewId(Long id) {
        return reviewDao.readByReviewId(id);
    }

    public Review update(Review review) {
        return reviewDao.update(review);
    }

    public Review deleteReviewById(Long id) {
        return reviewDao.deleteById(id);
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