package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.customAnnotation.IsAfterEarliestDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
@Data
@Builder
public class Film {
    private int id;
    @NotBlank
    private String name;
    @Size(max=200)
    private String description;
    @Positive
    private long duration;
    @IsAfterEarliestDate
    private LocalDate releaseDate;
}
