package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private Set<Long> setFriends;
}
