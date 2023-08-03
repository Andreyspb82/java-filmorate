package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    @PositiveOrZero
    private Integer id;

    @Email
    @NotNull
    private String email;

    @NotBlank
    @Pattern(regexp = "\\S+", message = "User login must not contain whitespaces.")
    private String login;

    private String name;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;
}
