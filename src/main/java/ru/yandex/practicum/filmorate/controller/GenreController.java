package ru.yandex.practicum.filmorate.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
@AllArgsConstructor
public class GenreController {

    private final MpaService mpaService;
    private final GenreService genreService;

    @GetMapping
    public List<Genre> getGenres() {
        return genreService.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreId(@PathVariable int id) {
        return genreService.getGenreId(id);
    }


}
