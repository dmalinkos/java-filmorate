package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import java.util.HashMap;

@Data
@Builder
public class Review {
    private Long id;
    @NotBlank(message = "Review.content is blank")
    private String content;
    @Builder.Default
    private Boolean isPositive = false;
    private Long userId;
    private Long filmId;
    //TODO
    //check if necessary or separate class
    /**
     * @impNote насколько я понимаю, на фронте юзер ставит лайк/дизлайк -> тогда мы апдейтим отзыв
     */
    @Builder.Default
    @Min(-1)
    @Max(1)
    private Integer isLiked = 0;
    @Builder.Default
    private Long useful = 0L;

    //TODO
    // check if this is necessary
    @Builder.Default
    private HashMap<Long, Boolean> userIdToReviewLike = new HashMap<>();
}