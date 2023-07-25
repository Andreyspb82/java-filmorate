package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mpa {

    @NotNull
    @PositiveOrZero
    private Integer id;

    @NotBlank
    private String name;

    public Mpa(Integer id) {
        this.id = id;
    }
}
