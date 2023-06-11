package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    boolean addFriend(int id, int friendId);

    boolean delFriend(Integer id, Integer friendId);

    Collection<User> getFriend(int id);

    Collection<User> getCommonFriends(Integer id, Integer otherId);
}
