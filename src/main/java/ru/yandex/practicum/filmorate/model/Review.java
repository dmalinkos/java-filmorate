package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {
    private Long reviewId;

    @NotBlank(message = "Пустое значение поля Review.content")
    private String content;

    @NotNull(message = "не задан тип отзыва")
    private Boolean isPositive;

    @NotNull(message = "не задан идентификатор пользователя")
    private Long userId;

    @NotNull(message = "не задан идентификатор фильма")
    private Long filmId;

    @Builder.Default
    private Long useful = 0L;
}