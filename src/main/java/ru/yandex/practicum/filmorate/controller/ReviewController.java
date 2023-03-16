package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("Получен POST-запрос к эндпоинту /reviews (добавление отзыва). Пользователь-id: {}; фильм-id: {}", review.getUserId(), review.getFilmId());
        return reviewService.add(review);
    }
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Получен PUT-запрос к эндпоинту /reviews (обновление отзыва). Отзыв-id: {}", review.getReviewId());
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public Review DeleteReview(@PathVariable("id") Long reviewId) {
        log.info("Получен DELETE-запрос к эндпоинту /reviews (удаление отзыва). id: {}", reviewId);
        return reviewService.deleteReviewById(reviewId);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable("id") Long reviewId) {
        log.info("Получен GET-запрос к эндпоинту /reviews (получение отзыва). id: {}", reviewId);
        return reviewService.readByReviewId(reviewId);
    }

    @GetMapping
    public List<Review> getReviews(
                        @RequestParam(name = "filmId", required = false) Long filmId,
                        @RequestParam(name = "count", required = false, defaultValue = "10") Integer count) {
        log.info("Получен GET-запрос к эндпоинту /reviews (получение списка отзывов)");
            return reviewService.getReviews(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public Review likeReviewByUser(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
            log.info("Получен PUT-запрос к эндпоинту /reviews (\"лайк\" отзыва). Отзыв-id: {}. Пользователь-id {}", reviewId, userId);
            Integer rateValueFromUser = 1;
            return reviewService.setNewRateFromUser(userId, reviewId, rateValueFromUser);
     }

    @PutMapping("{id}/dislike/{userId}")
    public Review dislikeReviewByUser(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        log.info("Получен PUT-запрос к эндпоинту /reviews (\"дислайк\" отзыва). Отзыв-id: {}. Пользователь-id {}", reviewId, userId);
        Integer rateValueFromUser = -1;
        return reviewService.setNewRateFromUser(userId, reviewId, rateValueFromUser);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Review deleteLikeByUser(@PathVariable("id") Long reviewId, Long userId) {
        log.info("Получен DELETE-запрос к эндпоинту /reviews...like (Пользователь-id {} удаляет оценку отзыву-id {}"
                , userId, reviewId);
        return reviewService.deleteRateFromUser(userId, reviewId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public Review deleteDislikeByUser(@PathVariable("id") Long reviewId, Long userId) {
        log.info("Получен DELETE-запрос к эндпоинту /reviews...dislike (Пользователь-id {} удаляет оценку отзыву-id {}"
                , userId, reviewId);
        return reviewService.deleteRateFromUser(userId, reviewId);
    }
}
