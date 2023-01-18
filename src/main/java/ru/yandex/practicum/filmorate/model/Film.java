package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
@Data
public class Film {
    int id;
    @NotBlank
    String name;
    @Size(max=200)
    String description;
    @PositiveOrZero
    long duration;
    LocalDate releaseDate;
}
