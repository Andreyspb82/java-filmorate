package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();


    @Override
    public Film putFilm(Film film) {
        films.put(film.getId(), film);
        log.info("Получен запрос POST /films, добавлен фильм");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильма с таким Id нет");
            throw new NotFoundException("Фильма с таким Id нет");
        }
        films.put(film.getId(), film);
        log.info("Получен запрос PUT /films, обновлен фильм");
        return film;
    }

    @Override
    public List<Film> getFilms() {
        log.info("Получен запрос GET /films, (список фильмов)");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmId(int id) {
        if (!films.containsKey(id)) {
            log.warn("Фильма с таким Id нет");
            throw new NotFoundException("Фильма с таким Id нет");
        }
        return films.get(id);
    }

    @Override
    public void removeFilmId(int id) {
        if (!films.containsKey(id)) {
            log.warn("Фильма с таким Id нет");
            throw new NotFoundException("Фильма с таким Id нет");
        }
        films.remove(id);
    }

}
