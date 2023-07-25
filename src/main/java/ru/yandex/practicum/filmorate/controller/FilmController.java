package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmId(@Valid @PathVariable int id) {
        return filmService.getFilmId(id);
    }

    @DeleteMapping("/{id}")
    public void removeFilmId(@Valid @PathVariable int id) {
        filmService.removeFilmId(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeFilm(@Valid @PathVariable("id") int filmId,
                            @Valid @PathVariable int userId) {
        filmService.addLikeFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFilm(@Valid @PathVariable("id") int filmId,
                               @Valid @PathVariable int userId) {
        filmService.removeLikeFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilmsByGenreAndYear(@RequestParam(name = "count", defaultValue = "10") int count,
                                                     @RequestParam(name = "genreId") Optional<Integer> genreId,
                                                     @RequestParam(name = "year") Optional<Integer> year) {
        return filmService.filmsByGenreAndYear(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@Valid @PathVariable("directorId") int directorId,
                                         @RequestParam(defaultValue = "ASC") String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> findCommonPopularFilms(@RequestParam int userId,
                                             @RequestParam int friendId) {
        return filmService.commonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> getFilmsBySearch(@RequestParam(name = "query") String query,
                                       @RequestParam(name = "by") List<String> by) {
        return filmService.searchFilms(query, by);

    }

}
