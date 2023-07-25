package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@Data
public class UserService {

    private final UserStorage userStorage;


    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.putUser(user);
    }

    public User updateUser(User user) {
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

    public List<Film> getFilmsRecommendations(int userId) {
        userStorage.getUserId(userId);
        return userStorage.getFilmsRecommendations(userId);
    }

    public List<Feed> getFeedsId(int userId) {
        userStorage.getUserId(userId);
        return userStorage.getFeedsId(userId);
    }
}
