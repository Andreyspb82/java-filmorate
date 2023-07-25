package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre {

    @NotNull
    @Positive
    private Integer id;

    @NotBlank
    private String name;

    public Genre(Integer id) {
        this.id = id;
    }
}
