package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Film {
    private Integer id;
    private String name;
    private LocalDate releaseDate;
    private String description;
    private int duration;
    private int rate;
    private Mpa mpa = new Mpa();
    private List<Genre> genres = new ArrayList<>();

    public Film() {
    }
}
