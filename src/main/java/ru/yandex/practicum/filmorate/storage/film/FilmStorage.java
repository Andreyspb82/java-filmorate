package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film putFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    List<Film> getFilmsByUserId(int userId);

    Film getFilmId(int id);

    void removeFilmId(int id);

    void addLikeFilm(int filmId, int userId);

    void removeLikeFilm(int filmId, int userId);

    List <Film> getFilmsRecommendations (int userId);


}
