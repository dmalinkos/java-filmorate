package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
public class User {
    int id;
    @Email
    String email;
    @NotBlank
    String login;
    String name;
    @PastOrPresent
    LocalDate birthday;
}
