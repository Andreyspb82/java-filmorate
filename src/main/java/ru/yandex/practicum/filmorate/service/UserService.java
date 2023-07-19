package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@Data
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User user) {
        validationUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.putUser(user);
    }

    public User updateUser(User user) {
        validationUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserId(int id) {
        return userStorage.getUserId(id);
    }

    public void removeUserId(int id) {
        userStorage.removeUserId(id);
    }

    public void addFriendId(int userId, int friendId) {
        userStorage.getUserId(userId);
        userStorage.getUserId(friendId);

        userStorage.addFriendId(userId, friendId);
    }

    public void removeFriendId(int userId, int friendId) {
        userStorage.getUserId(userId);
        userStorage.getUserId(friendId);

        userStorage.removeFriendId(userId, friendId);

    }

    public List<User> getFriends(int userId) {
        userStorage.getUserId(userId);
        return userStorage.getFreinds(userId);
    }


    public List<User> getCommonFriends(int userId, int otherId) {
        userStorage.getUserId(userId);
        userStorage.getUserId(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }


    private void validationUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            logValidationUser();
            throw new ValidationException("Некорректный адрес электронной почты");
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            logValidationUser();
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            logValidationUser();
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void logValidationUser() {
        log.warn("Валидация пользователя не пройдена");
    }

}
