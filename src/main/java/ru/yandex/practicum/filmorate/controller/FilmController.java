package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;

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
    public List<Film> findPopularFilmsByGenreAndYear(@RequestParam(name = "count", defaultValue = "10") int count,
                                                     @RequestParam(name = "genreId") Optional<Integer> genreId,
                                                     @RequestParam(name = "year") Optional<Integer> year) {
        return filmService.filmsByGenreAndYear(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable("directorId") int directorId, @RequestParam(defaultValue = "ASC") String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> findCommonPopularFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.commonFilms(userId, friendId);
    }

}
