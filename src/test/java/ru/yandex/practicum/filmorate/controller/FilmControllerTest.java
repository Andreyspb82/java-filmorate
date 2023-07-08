package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FilmController filmController;

    public FilmControllerTest(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Film filmTest;
    private Mpa mpaTest;
    private Genre genreTest;

    @BeforeEach
    void setUp() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        UserService userService = new UserService(userStorage);
        FilmService filmService = new FilmService(filmStorage, userService);
        filmController = new FilmController(filmService);

        mpaTest = new Mpa(1, "G");
        genreTest = new Genre(1, "Комедия");
        List<Genre> genres = new ArrayList<>();
        genres.add(genreTest);
        filmTest = new Film(null, "name", LocalDate.of(2000, 1, 1),
                "description", 60, 2, mpaTest, genres);
    }

    @Test
    void createFilm() {
        Film createFilm = filmController.createFilm(filmTest);
        assertEquals(1, createFilm.getId(), "Неверный Id фильма");
        assertEquals("name", createFilm.getName(), "Неверное название фильма");
        assertEquals(LocalDate.of(2000, 1, 1), createFilm.getReleaseDate(), "Неверная дата релиза");
        assertEquals("description", createFilm.getDescription(), "Неверное описание фильма");
        assertEquals(60, createFilm.getDuration(), "Неверная продолжительность фильма");
        assertEquals(2, createFilm.getRate(), "Неверное количество лайков у фильма");
        assertEquals(1, createFilm.getMpa().getId(), "Неверный Id MPA фильма");
        assertEquals("G", createFilm.getMpa().getName(), "Неверное название MPA фильма");
        assertEquals(1, createFilm.getGenres().get(0).getId(), "Неверное Id жанра фильма");
        assertEquals("Комедия", createFilm.getGenres().get(0).getName(), "Неверное название жанра фильма");
    }

    @Test
    void shouldReturnErrorEmptyName() {
        filmTest.setName(null);
        ValidationException ex = assertThrows(ValidationException.class, new Executable()  {
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

    @Test
    void shouldReturnFilmById() {
        Film createFilm = filmController.createFilm(filmTest);
        Film filmId = filmController.getFilmId(1);
        assertEquals(createFilm, filmId, "Фильмы на совпадают");
    }

    @Test
    void shouldReturnErrorInvalidFilmId() {
        Film createFilm = filmController.createFilm(filmTest);
        NotFoundException ex = assertThrows(NotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmController.getFilmId(2);
            }
        });
        assertEquals("Фильма с Id = " + 2 + " нет",
                ex.getMessage(), "Проверка получения фильма по несуществующему Id");
    }

    @Test
    void shouldRemoveFilmById() {
        Film createFilm = filmController.createFilm(filmTest);
        assertFalse(filmController.getFilms().isEmpty(), "Список фильмов пустой");
        filmController.removeFilmId(1);
        assertTrue(filmController.getFilms().isEmpty(), "Список фильмов не пустой");
    }

}