package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final LocalDate EARLIEST_AVAILABLE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    private int filmId = 0;

    private int getNextId() {
        return ++filmId;
    }


    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос GET /films, (список фильмов)");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        validationFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Получен запрос POST /films, добавлен фильм");

        return film;
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        validationFilm(film);
        if (!films.containsKey(film.getId())) {
            log.warn("Валидация фильма не пройдена");
            throw new ValidationException("Фильма с таким Id нет");
        }
        films.put(film.getId(), film);
        log.info("Получен запрос PUT /films, обновлен фильм");
        return film;
    }

    private void validationFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация фильма не пройдена");
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.warn("Валидация фильма не пройдена");
            throw new ValidationException("Описание фильма не должно превышать 200 символов");
        } else if (film.getReleaseDate().isBefore(EARLIEST_AVAILABLE_DATE)) {
            log.warn("Валидация фильма не пройдена");
            throw new ValidationException("Дата релиза фильма должна быть не раньше чем 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            log.warn("Валидация фильма не пройдена");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }


}
