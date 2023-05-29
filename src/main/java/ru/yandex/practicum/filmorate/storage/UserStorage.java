package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    Map<Integer, User> getUsers();

    User getUserById(Integer id) throws ValidationException;

    User addUser(User user) throws ValidationException;

    User updateUser(User user) throws ValidationException;

    User deleteUser(User user) throws ValidationException;

    User deleteUserById(Integer id) throws IllegalArgumentException;
}
