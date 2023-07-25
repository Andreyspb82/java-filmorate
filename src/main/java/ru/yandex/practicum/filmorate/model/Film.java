package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.IsAfter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    @PositiveOrZero
    private Integer id;

    @NotBlank
    private String name;

    @NotNull
    @IsAfter(year = 1895, month = 11, day = 28)
    private LocalDate releaseDate;

    @NotBlank
    @Size(max = 200, message = "Description length must be less than 200 characters.")
    private String description;

    @Positive
    private int duration;

    private int rate;

    @NotNull
    private Mpa mpa = new Mpa();

    private List<Genre> genres = new ArrayList<>();

    private List<Director> directors = new ArrayList<>();
}
