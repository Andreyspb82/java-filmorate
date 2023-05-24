package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void createUser() {
        User createUser = userController.createUser(new User(-1, "mail@mail.com",
                "login", "name", LocalDate.of(2000, 1, 1)));
        assertEquals(1, createUser.getId(), "Неверный Id пользователя");
        assertEquals("mail@mail.com", createUser.getEmail(), "Неверный email пользователя");
        assertEquals("login", createUser.getLogin(), "Неверный логин пользователя");
        assertEquals("name", createUser.getName(), "Неверное имя пользователя");
        assertEquals(LocalDate.of(2000, 1, 1), createUser.getBirthday(), "Неверная дата рождения");
    }

    @Test
    void shouldReturnErrorEmptyEmail() {
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                User createUser = userController.createUser(new User(-1, "",
                        "login", "name", LocalDate.of(2000, 1, 1)));
            }
        });
        assertEquals("Некорректный адрес электронной почты",
                ex.getMessage(), "Проверке валидации по пустой электронной почте");
    }

    @Test
    void shouldReturnErrorInvalidEmail() {
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                User createUser = userController.createUser(new User(-1, "mail.com",
                        "login", "name", LocalDate.of(2000, 1, 1)));
            }
        });
        assertEquals("Некорректный адрес электронной почты",
                ex.getMessage(), "Проверке валидации по содержанию @ в электронной почте");
    }

    @Test
    void shouldReturnErrorEmptyLogin() {
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                User createUser = userController.createUser(new User(-1, "mail@mail.com",
                        "", "name", LocalDate.of(2000, 1, 1)));
            }
        });
        assertEquals("Логин не может быть пустым или содержать пробелы",
                ex.getMessage(), "Проверке валидации по пустому логину");
    }

    @Test
    void shouldReturnErrorInvalidLogin() {
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                User createUser = userController.createUser(new User(-1, "mail@mail.com",
                        "login login", "name", LocalDate.of(2000, 1, 1)));
            }
        });
        assertEquals("Логин не может быть пустым или содержать пробелы",
                ex.getMessage(), "Проверке валидации по пробелам в логине");
    }

    @Test
    void shouldReplaceNameWithLogin() {
        User createUser = userController.createUser(new User(-1, "mail@mail.com",
                "login", "", LocalDate.of(2000, 1, 1)));
        assertEquals("login", createUser.getName(), "Неверное имя пользователя");
    }

    @Test
    void shouldReturnErrorInvalidBirthday() {
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                User createUser = userController.createUser(new User(-1, "mail@mail.com",
                        "login", "name", LocalDate.of(2100, 1, 1)));
            }
        });
        assertEquals("Дата рождения не может быть в будущем",
                ex.getMessage(), "Проверке валидации по дате рождения");
    }

}