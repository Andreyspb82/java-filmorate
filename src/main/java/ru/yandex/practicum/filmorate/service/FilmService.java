package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    private static final LocalDate EARLIEST_AVAILABLE_DATE = LocalDate.of(1895, 12, 28);

    public Film createFilm(Film film) {
        validationFilm(film);
        return filmStorage.putFilm(film);
    }

    public Film updateFilm(Film film) {
        validationFilm(film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmId(int id) {
        return filmStorage.getFilmId(id);
    }

    public void removeFilmId(int id) {
        filmStorage.removeFilmId(id);
    }

    public void addLikeFilm(int filmId, int userId) {
        filmStorage.getFilmId(filmId);
        userService.getUserId(userId);

        filmStorage.addLikeFilm(filmId, userId);
    }

    public void removeLikeFilm(int filmId, int userId) {
        filmStorage.getFilmId(filmId);
        userService.getUserId(userId);
        filmStorage.removeLikeFilm(filmId, userId);
    }

    public List<Film> ratingFilms(int count) {
        List<Film> ratingFilms = new ArrayList<>(filmStorage.getFilms());


        if (ratingFilms.isEmpty()) {
            log.warn("Список пустой");
            throw new NotFoundException("Список пустой");
        }

        Collections.sort(ratingFilms, new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o2.getRate() - o1.getRate();
            }
        });

        return ratingFilms.stream()
                .limit(count)
                .collect(Collectors.toList());

    }

    public List<Film> getFilmsByDirector(int id, String sort) {
        return filmStorage.getFilmsByDirector(id, sort);
    }

    public List<Film> commonFilms(int userId, int friendId) {
        Set<Film> userFilms = new HashSet<>(filmStorage.getFilmsByUserId(userId));
        Set<Film> friendFilms = new HashSet<>(filmStorage.getFilmsByUserId(friendId));
        userFilms.retainAll(friendFilms);
        return new ArrayList<>(userFilms);
    }


    private void validationFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            logValidationFilm();
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            logValidationFilm();
            throw new ValidationException("Описание фильма не должно превышать 200 символов");
        } else if (film.getReleaseDate().isBefore(EARLIEST_AVAILABLE_DATE)) {
            logValidationFilm();
            throw new ValidationException("Дата релиза фильма должна быть не раньше чем 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            logValidationFilm();
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private void logValidationFilm() {
        log.warn("Валидация фильма не пройдена");
    }

}
