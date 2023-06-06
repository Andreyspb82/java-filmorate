package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    public final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User put(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserId(@PathVariable int id) {
        return userService.getUserId(id);
    }

    @DeleteMapping("/{id}")
    public void removeUserId(@PathVariable int id) {
        userService.removeUserId(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void putFriendId(@PathVariable("id") int userId, @PathVariable int friendId) {
        userService.addFriendId(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriendId(@PathVariable("id") int userId, @PathVariable int friendId) {
        userService.removeFriendId(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") int userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") int userId, @PathVariable int otherId) {
        return userService.getCommonFriends(userId, otherId);
    }

}
