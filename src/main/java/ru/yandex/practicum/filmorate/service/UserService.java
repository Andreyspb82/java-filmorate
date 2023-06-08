package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Data
public class UserService {
    private final UserStorage userStorage;

    private int userId = 0;

    private int getNextId() {
        return ++userId;
    }

    public User createUser(User user) {
        validationUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
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
        User user = userStorage.getUserId(userId);
        User userFriend = userStorage.getUserId(friendId);

        user.addFriend(friendId);
        userFriend.addFriend(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(userFriend);
    }

    public void removeFriendId(int userId, int friendId) {
        User user = userStorage.getUserId(userId);
        User userFriend = userStorage.getUserId(friendId);

        user.removeFriend(friendId);
        userFriend.removeFriend(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(userFriend);
    }

    public List<User> getFriends(int userId) {
        User user = userStorage.getUserId(userId);

        List<Integer> listId = new ArrayList<>(user.getFriends());
        List<User> listFriends = new ArrayList<>();

        for (Integer id : listId) {
            listFriends.add(userStorage.getUserId(id));
        }
        return listFriends;
    }


    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getUserId(userId);
        User userOther = userStorage.getUserId(otherId);

        List<Integer> listUserId = new ArrayList<>(user.getFriends());
        List<Integer> listOtherId = new ArrayList<>(userOther.getFriends());
        List<Integer> listCommonId = new ArrayList<>();

        for (Integer id1 : listUserId) {
            for (Integer id2 : listOtherId) {
                if (id1.equals(id2)) {
                    listCommonId.add(id1);
                }
            }
        }
        List<User> listCommonFriends = new ArrayList<>();

        for (Integer id : listCommonId) {
            listCommonFriends.add(userStorage.getUserId(id));
        }
        return listCommonFriends;
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
        log.warn("Валидация ползователя не пройдена");
    }


}
