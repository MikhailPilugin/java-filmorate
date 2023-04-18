package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    private InMemoryUserStorage inMemoryUserStorage;
    private UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @GetMapping
    public Map<Integer, User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        return inMemoryUserStorage.getUsers().get(id);
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) throws ValidationException {
        return inMemoryUserStorage.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) throws ValidationException {
        return inMemoryUserStorage.updateUser(user);
    }

    @DeleteMapping
    public User deleteUser(@RequestBody @Valid User user) throws ValidationException {
        return inMemoryUserStorage.deleteUser(user);
    }

    @PutMapping("/{id}/friends/{otherId}")
    public User addFriend(@PathVariable long id, long otherId) {
        return inMemoryUserStorage.userService.addFriend(id, otherId);
    }

    @DeleteMapping("/{id}/friends/{otherId}")
    public User delFriend(@PathVariable long id, long otherId) {
        return inMemoryUserStorage.userService.delFriend(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public Set<Long> getFriends(@PathVariable long id) {
        return inMemoryUserStorage.userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<Long> getCommonFriends(@PathVariable long id, long otherId) {
        return inMemoryUserStorage.userService.getCommonFriends(id, otherId);
    }
}
