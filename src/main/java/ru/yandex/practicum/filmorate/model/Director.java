package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
public class Director {

    @PositiveOrZero
    private int id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    public Director(int id) {
        this.id = id;
    }
}
