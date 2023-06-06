package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film putFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilmId(int id);

    void removeFilmId(int id);


}
