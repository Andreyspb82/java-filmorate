package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private Film filmTest;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        filmTest = new Film(null, "name", "description",
                LocalDate.of(2000, 1, 1), 60);
    }

    @Test
    void createFilm() {
        Film createFilm = filmController.createFilm(filmTest);
        assertEquals(1, filmTest.getId(), "Неверный Id фильма");
        assertEquals("name", filmTest.getName(), "Неверное название фильма");
        assertEquals("description", filmTest.getDescription(), "Неверное описание фильма");
        assertEquals(LocalDate.of(2000, 1, 1), filmTest.getReleaseDate(), "Неверная дата релиза");
        assertEquals(60, filmTest.getDuration(), "Неверная продолжительность фильма");
    }

    @Test
    void shouldReturnErrorEmptyName() {
        filmTest.setName(null);
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmController.createFilm(filmTest);
            }
        });
        assertEquals("Название фильма не может быть пустым",
                ex.getMessage(), "Проверка валидации на пустое имя фильма");
    }

    @Test
    void shouldReturnErrorDescription() {
        filmTest.setDescription("a".repeat(201));
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmController.createFilm(filmTest);
            }
        });
        assertEquals("Описание фильма не должно превышать 200 символов",
                ex.getMessage(), "Проверка валидации на количество символов в описании");
    }

    @Test
    void shouldReturnErrorRelease() {
        filmTest.setReleaseDate(LocalDate.of(1895, 12, 27));
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmController.createFilm(filmTest);
            }
        });
        assertEquals("Дата релиза фильма должна быть не раньше чем 28 декабря 1895 года",
                ex.getMessage(), "Проверка валидации на дату релиза фильма");
    }

    @Test
    void shouldReturnErrorDuration() {
        filmTest.setDuration(-1);
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmController.createFilm(filmTest);
            }
        });
        assertEquals("Продолжительность фильма должна быть положительной",
                ex.getMessage(), "Проверка валидации на продолжительность фильма");
    }

}