package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film putFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    List<Film> getFilmsByGenreAndYear(Optional<Integer> genreId, Optional<Integer> year);

    List<Film> getFilmsByGenre(Optional<Integer> genreId);

    List<Film> getFilmsByYear(Optional<Integer> year);

    List<Film> getFilmsByUserId(int userId);

    Film getFilmId(int id);

    void removeFilmId(int id);

    void addLikeFilm(int filmId, int userId);

    void removeLikeFilm(int filmId, int userId);

    List<Film> getFilmsByDirector(int id, String sort);

    List<Film> getFilmsRecommendations(int userId);

    List<Film> searchFilmsByDirectorAndTitle(String query);

    List<Film> searchFilmsByDirector(String query);

    List<Film> searchFilmsByTitle(String query);

}
