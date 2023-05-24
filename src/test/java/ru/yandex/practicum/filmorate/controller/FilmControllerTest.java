package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void createFilm() {
        Film createFilm = filmController.createFilm(new Film(-1, "name", "description",
                LocalDate.of(2000, 1, 1), 60));
        assertEquals(1, createFilm.getId(), "Неверный Id фильма");
        assertEquals("name", createFilm.getName(), "Неверное название фильма");
        assertEquals("description", createFilm.getDescription(), "Неверное описание фильма");
        assertEquals(LocalDate.of(2000, 1, 1), createFilm.getReleaseDate(), "Неверная дата релиза");
        assertEquals(60, createFilm.getDuration(), "Неверная продолжительность фильма");
    }

    @Test
    void shouldReturnErrorEmptyName() {
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Film createFilm = filmController.createFilm(new Film(-1, "", "description",
                        LocalDate.of(2000, 1, 1), 60));
            }
        });
        assertEquals("Название фильма не может быть пустым",
                ex.getMessage(), "Проверке валидации на пустое имя фильма");
    }

    @Test
    void shouldReturnErrorDescription() {
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Film createFilm = filmController.createFilm(new Film(-1, "name", "Пятеро друзей" +
                        "( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина" +
                        " Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который" +
                        " за время «своего отсутствия», стал кандидатом Коломбани.",
                        LocalDate.of(2000, 1, 1), 60));
            }
        });
        assertEquals("Описание фильма не должно превышать 200 символов",
                ex.getMessage(), "Проверке валидации на количество символов в описании");
    }

    @Test
    void shouldReturnErrorRelease() {
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Film createFilm = filmController.createFilm(new Film(-1, "name", "description",
                        LocalDate.of(1895, 12, 27), 60));
            }
        });
        assertEquals("Дата релиза фильма должна быть не раньше чем 28 декабря 1895 года",
                ex.getMessage(), "Проверке валидации на дату релиза фильма");
    }

    @Test
    void shouldReturnErrorDuration() {
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Film createFilm = filmController.createFilm(new Film(-1, "name", "description",
                        LocalDate.of(2000, 1, 1), -1));
            }
        });
        assertEquals("Продолжительность фильма должна быть положительной",
                ex.getMessage(), "Проверке валидации на продолжительность фильма");
    }

}