package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    public boolean addFriend(int id, int friendId);

    public boolean delFriend(Integer id, Integer friendId);

    public Collection<User> getFriend(int id);

    public Collection<User> getCommonFriends(Integer id, Integer otherId);
}
