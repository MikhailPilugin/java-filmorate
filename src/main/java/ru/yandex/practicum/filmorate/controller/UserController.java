package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@ResponseBody
public class UserController {
    private InMemoryUserStorage inMemoryUserStorage;
    private UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return inMemoryUserStorage.userService.userMap.values();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable long id) throws ValidationException {
        User user = null;
        boolean userNotFound = true;

        for (Map.Entry<Integer, User> integerUserEntry : inMemoryUserStorage.userService.userMap.entrySet()) {
            if (integerUserEntry.getKey() == id) {
                user = integerUserEntry.getValue();
                userNotFound = false;
            }
        }

        if (userNotFound) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        return user;
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
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        System.out.println(inMemoryUserStorage.userService.userMap.get(id));
        System.out.println(inMemoryUserStorage.userService.userMap.get(friendId));

        return inMemoryUserStorage.userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User delFriend(@PathVariable @Valid Long id, Long friendId) {
        return inMemoryUserStorage.userService.delFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public Set<Long> getFriends(@PathVariable @Valid Long id) {
        return inMemoryUserStorage.userService.userMap.get(id).getFriends();
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Set<Long> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return inMemoryUserStorage.userService.getCommonFriends(id, otherId);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse("error", "Передан некорректный параметр");
    }

    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInterruptedException(final NullPointerException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ErrorResponse("error", e.getMessage());
    }
}
