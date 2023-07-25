package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User putUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUserId(int id);

    void removeUserId(int id);

    void addFriendId(int userId, int friendId);

    List<User> getFreinds(int id);

    void removeFriendId(int userId, int friendId);

    List<User> getCommonFriends(int userId, int otherId);

    List<Film> getFilmsRecommendations(int userId);

    List<Feed> getFeedsId(int userId);
}
