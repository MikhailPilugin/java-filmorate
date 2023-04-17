package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @GetMapping
    public Map<Integer, User> getUsers() {
        return inMemoryUserStorage.getUsers();
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
}
