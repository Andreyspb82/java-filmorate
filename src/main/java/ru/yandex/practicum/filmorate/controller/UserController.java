package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    public final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserId(@Valid @PathVariable int id) {
        return userService.getUserId(id);
    }

    @DeleteMapping("/{id}")
    public void removeUserId(@Valid @PathVariable int id) {
        userService.removeUserId(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void putFriendId(@Valid @PathVariable("id") int userId,
                            @Valid @PathVariable int friendId) {
        userService.addFriendId(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriendId(@Valid @PathVariable("id") int userId,
                               @Valid @PathVariable int friendId) {
        userService.removeFriendId(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@Valid @PathVariable("id") int userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@Valid @PathVariable("id") int userId,
                                       @Valid @PathVariable int otherId) {
        return userService.getCommonFriends(userId, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getFilmsRecommendations(@Valid @PathVariable("id") int userId) {
        return userService.getFilmsRecommendations(userId);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getFeedsId(@Valid @PathVariable("id") int userId) {
        return userService.getFeedsId(userId);
    }


}
