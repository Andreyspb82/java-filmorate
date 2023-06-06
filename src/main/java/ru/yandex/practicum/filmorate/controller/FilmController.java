package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmId(@PathVariable int id) {
        return filmService.getFilmId(id);
    }

    @DeleteMapping("/{id}")
    public void removeFilmId(@PathVariable int id) {
        filmService.removeFilmId(id);
    }


    @PutMapping("/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        filmService.addLikeFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        filmService.removeLikeFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> ratingFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.ratingFilms(count);
    }


}
