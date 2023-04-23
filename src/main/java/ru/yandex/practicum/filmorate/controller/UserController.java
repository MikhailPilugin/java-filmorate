package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@ResponseBody
public class UserController {
    private InMemoryUserStorage inMemoryUserStorage;
    private UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return inMemoryUserStorage.getUsers().values();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Integer id) throws ValidationException {
        return inMemoryUserStorage.getUserById(id);
    }

    @PostMapping("/users")
    public User addUser(@RequestBody @Valid User user) throws ValidationException {
        return inMemoryUserStorage.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody @Valid User user) throws ValidationException {
        return inMemoryUserStorage.updateUser(user);
    }

    @DeleteMapping("/users")
    public User deleteUser(@RequestBody @Valid User user) throws ValidationException {
        return inMemoryUserStorage.deleteUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public boolean addFriend(@PathVariable Integer id, @PathVariable Integer friendId) throws ValidationException {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public boolean delFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.delFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        return userService.getFriend(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse("error", "Передан некорректный параметр");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInterruptedException(final NullPointerException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ErrorResponse("error", e.getMessage());
    }
}
