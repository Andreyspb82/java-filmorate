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
    private User userTest;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        userTest = new User(null, "mail@mail.com",
                "login", "name", LocalDate.of(2000, 1, 1));
    }

    @Test
    void createUser() {
        User createUser = userController.createUser(userTest);
        assertEquals(1, createUser.getId(), "Неверный Id пользователя");
        assertEquals("mail@mail.com", createUser.getEmail(), "Неверный email пользователя");
        assertEquals("login", createUser.getLogin(), "Неверный логин пользователя");
        assertEquals("name", createUser.getName(), "Неверное имя пользователя");
        assertEquals(LocalDate.of(2000, 1, 1), createUser.getBirthday(), "Неверная дата рождения");
    }

    @Test
    void shouldReturnErrorEmptyEmail() {
        userTest.setEmail(null);
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.createUser(userTest);
            }
        });
        assertEquals("Некорректный адрес электронной почты",
                ex.getMessage(), "Проверка валидации по пустой электронной почте");
    }

    @Test
    void shouldReturnErrorInvalidEmail() {
        userTest.setEmail("mail.com");
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.createUser(userTest);
            }
        });
        assertEquals("Некорректный адрес электронной почты",
                ex.getMessage(), "Проверка валидации по содержанию @ в электронной почте");
    }

    @Test
    void shouldReturnErrorEmptyLogin() {
        userTest.setLogin(null);
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.createUser(userTest);
            }
        });
        assertEquals("Логин не может быть пустым или содержать пробелы",
                ex.getMessage(), "Проверка валидации по пустому логину");
    }

    @Test
    void shouldReturnErrorInvalidLogin() {
        userTest.setLogin("login login");
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.createUser(userTest);
            }
        });
        assertEquals("Логин не может быть пустым или содержать пробелы",
                ex.getMessage(), "Проверка валидации по пробелам в логине");
    }

    @Test
    void shouldReplaceNameWithLogin() {
        userTest.setName(null);
        User createUser = userController.createUser(userTest);
        assertEquals("login", createUser.getName(), "Неверное имя пользователя");
    }

    @Test
    void shouldReturnErrorInvalidBirthday() {
        userTest.setBirthday(LocalDate.of(2100, 1, 1));
        ValidationException ex = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.createUser(userTest);
            }
        });
        assertEquals("Дата рождения не может быть в будущем",
                ex.getMessage(), "Проверка валидации по дате рождения");
    }

}