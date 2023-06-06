package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();


    @Override
    public User putUser(User user) {
        users.put(user.getId(), user);
        log.info("Получен запрос POST /users, добавлен пользователь");
        return user;
    }


    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователя с таким Id нет");
            throw new NotFoundException("Пользователя с таким Id нет");
        }
        log.info("Получен запрос PUT /users, обновлен пользователь");
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        log.info("Получен запрос GET /users, (список ползователей)");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserId(int id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователя с таким Id нет");
            throw new NotFoundException("Пользователя с таким Id нет");
        }
        return users.get(id);
    }

    @Override
    public void removeUserId(int id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователя с таким Id нет");
            throw new NotFoundException("Пользователя с таким Id нет");
        }
        users.remove(id);
    }

}
