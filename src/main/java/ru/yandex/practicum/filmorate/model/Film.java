package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
@AllArgsConstructor
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private final Set<Integer> like = new TreeSet<>();


    public void addLike(int idUser) {
        like.add(idUser);
    }

    public void removeLike(int idUser) {
        like.remove(idUser);
    }

}
