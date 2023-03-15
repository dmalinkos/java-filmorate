package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.customAnnotation.IsAfterEarliestDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @Positive
    private long duration;
    @IsAfterEarliestDate
    private LocalDate releaseDate;
    private Mpa mpa;
    private Set<Genre> genres;
    private Set<Long> likesSet;
    private List<Director> directors;
}
