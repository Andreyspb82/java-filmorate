package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    int userId = 0;

    private int getNextId() {
        return ++userId;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Получен запрос GET /users, (список ползователей)");
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (validationUser(user)) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("Получен запрос POST /users, добавлен пользователь");
        }
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        if (validationUser(user)) {
            if (!users.containsKey(user.getId())) {
                log.warn("Валидация ползователя не пройдена");
                throw new ValidationException("Пользователя с таким Id нет");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            log.info("Получен запрос PUT /users, обновлен пользователь");
            users.put(user.getId(), user);
        }
        return user;
    }

    private boolean validationUser(User user) {
        boolean check = false;
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Валидация ползователя не пройдена");
            throw new ValidationException("Некорректный адрес электронной почты");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Валидация ползователя не пройдена");
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация ползователя не пройдена");
            throw new ValidationException("Дата рождения не может быть в будущем");
        } else {
            check = true;
        }
        return check;
    }
}
