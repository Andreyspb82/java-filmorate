package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final LocalDate AFTER_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    int filmId = 0;

    private int getNextId() {
        return ++filmId;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Получен запрос GET /films, (список фильмов)");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        if (validationFilm(film)) {
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Получен запрос POST /films, добавлен фильм");
        }
        return film;
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        if (validationFilm(film)) {
            if (!films.containsKey(film.getId())) {
                log.warn("Валидация фильма не пройдена");
                throw new ValidationException("Фильма с таким Id нет");
            }
            films.put(film.getId(), film);
            log.info("Получен запрос PUT /films, обновлен фильм");
        }
        return film;
    }

    private boolean validationFilm(Film film) {
        boolean check = false;

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация фильма не пройдена");
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.warn("Валидация фильма не пройдена");
            throw new ValidationException("Описание фильма не должно превышать 200 символов");
        } else if (film.getReleaseDate().isBefore(AFTER_DATE)) {
            log.warn("Валидация фильма не пройдена");
            throw new ValidationException("Дата релиза фильма должна быть не раньше чем 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            log.warn("Валидация фильма не пройдена");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        } else {
            check = true;
        }
        return check;
    }


}
