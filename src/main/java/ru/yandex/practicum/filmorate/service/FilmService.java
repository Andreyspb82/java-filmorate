package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        return filmStorage.putFilm(film);
    }

    public Film updateFilm(Film film) {
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

    public List<Film> getFilmsByDirector(int id, String sort) {
        return filmStorage.getFilmsByDirector(id, sort);
    }

    public List<Film> commonFilms(int userId, int friendId) {
        Set<Film> userFilms = new HashSet<>(filmStorage.getFilmsByUserId(userId));
        Set<Film> friendFilms = new HashSet<>(filmStorage.getFilmsByUserId(friendId));
        userFilms.retainAll(friendFilms);
        return new ArrayList<>(userFilms);
    }

    public List<Film> searchFilms(String query, List<String> by) {
        List<Film> filmsPopular = new ArrayList<>();
        if (by.size() > 1) {
            if ((by.get(0).equals("director") || by.get(0).equals("title")) && (by.get(1).equals("title")
                    || by.get(1).equals("director")) && !(by.get(0).equals(by.get(1)))) {
                filmsPopular = filmStorage.searchFilmsByDirectorAndTitle(query);
            }
        } else if (by.size() == 1) {
            if (by.get(0).equals("director")) {
                filmsPopular = filmStorage.searchFilmsByDirector(query);
            } else if (by.get(0).equals("title")) {
                filmsPopular = filmStorage.searchFilmsByTitle(query);
            } else {
                return filmsPopular;
            }
        } else {
            return filmsPopular;
        }
        Collections.sort(filmsPopular, new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o2.getRate() - o1.getRate();
            }
        });
        return filmsPopular.stream()
                .collect(Collectors.toList());
    }

    public List<Film> filmsByGenreAndYear(int count, Optional<Integer> genreId, Optional<Integer> year) {
        List<Film> filmsPopular = new ArrayList<>();
        if (genreId.isPresent() && year.isPresent()) {
            filmsPopular = new ArrayList<>(filmStorage.getFilmsByGenreAndYear(genreId, year));
        } else if (genreId.isPresent()) {
            filmsPopular = new ArrayList<>(filmStorage.getFilmsByGenre(genreId));
        } else if (year.isPresent()) {
            filmsPopular = new ArrayList<>(filmStorage.getFilmsByYear(year));
        } else {
            filmsPopular = new ArrayList<>(filmStorage.getFilms());
            if (filmsPopular.isEmpty()) {
                log.warn("Список пустой");
                throw new NotFoundException("Список пустой");
            }
            Collections.sort(filmsPopular, new Comparator<Film>() {
                @Override
                public int compare(Film o1, Film o2) {
                    return o2.getRate() - o1.getRate();
                }
            });
            return filmsPopular.stream()
                    .limit(count)
                    .collect(Collectors.toList());
        }
        return filmsPopular;
    }
}
