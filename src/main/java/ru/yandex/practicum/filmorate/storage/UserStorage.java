package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    public List<User> getUsers();
    public User addUser(User user) throws ValidationException;

    public User updateUser(User user) throws ValidationException;

    public User deleteUser(User user) throws ValidationException;
}
