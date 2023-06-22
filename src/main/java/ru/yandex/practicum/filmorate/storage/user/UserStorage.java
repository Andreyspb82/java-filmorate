package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User putUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUserId(int id);

    void removeUserId(int id);


}
