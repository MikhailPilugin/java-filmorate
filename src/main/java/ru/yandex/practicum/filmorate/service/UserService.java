package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Service
public class UserService {
    InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public boolean addFriend(int id, int friendId) {
        User user = null;
        User friend = null;
        int userIndex = 0;
        int friendIndex = 0;
        boolean isFriendsAdd = false;

        if (id < 0 || friendId < 0) {
            throw new IllegalArgumentException("Отрицательное значение переменной пути");
        }

        for (int i = 0; i < inMemoryUserStorage.userMap.size(); i++) {
            if (inMemoryUserStorage.userMap.get(i).getId() == id) {
                userIndex = i;
                user = inMemoryUserStorage.userMap.get(i);
            } else if (inMemoryUserStorage.userMap.get(i).getId() == friendId) {
                friendIndex = i;
                friend = inMemoryUserStorage.userMap.get(i);
            }
        }

        int statusAddUser = user.setFriends(friend.getId());
        int statusAddFriend = friend.setFriends(user.getId());

        if (statusAddUser == 1 && statusAddFriend == 1) {
            isFriendsAdd = true;
        }

        inMemoryUserStorage.userMap.replace(userIndex, user);
        inMemoryUserStorage.userMap.replace(friendIndex, friend);

        return isFriendsAdd;
    }

    public boolean delFriend(Integer id, Integer friendId) {
        User user = null;
        User friend = null;
        int userIndex = 0;
        int friendIndex = 0;
        boolean isFriendsDelete = false;

        if (id < 0 || friendId < 0) {
            throw new IllegalArgumentException("Отрицательное значение переменной пути");
        }

        for (int i = 0; i < inMemoryUserStorage.userMap.size(); i++) {
            if (inMemoryUserStorage.userMap.get(i).getId() == id) {
                userIndex = i;
                user = inMemoryUserStorage.userMap.get(i);
            } else if (inMemoryUserStorage.userMap.get(i).getId() == friendId) {
                friendIndex = i;
                friend = inMemoryUserStorage.userMap.get(i);
            }
        }

        int delStatusUser = user.removeFriends(friend.getId());
        int delStatusFriend = friend.removeFriends(user.getId());

        if (delStatusUser == 1 && delStatusFriend == 1) {
            isFriendsDelete = true;
        }

        inMemoryUserStorage.userMap.replace(userIndex, user);
        inMemoryUserStorage.userMap.replace(friendIndex, friend);

        return isFriendsDelete;
    }

    public Collection<User> getFriend(int id) {
        User user = null;
        List<User> userFriendsList = new ArrayList<>();
        Set<Integer> userFriendsSet;

        for (Map.Entry<Integer, User> integerUserEntry : inMemoryUserStorage.userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == id) {
                user = integerUserEntry.getValue();
                break;
            }
        }

        userFriendsSet = user.getFriends();

        for (Map.Entry<Integer, User> integerUserEntry : inMemoryUserStorage.userMap.entrySet()) {
            if (userFriendsSet.contains(integerUserEntry.getValue().getId())) {
                userFriendsList.add(integerUserEntry.getValue());
            }
        }
        return userFriendsList;
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        User user;
        Set<Integer> commonFriends = null;
        List<User> listCommonFriends = new ArrayList<>();
        Set<Integer> setId = null;
        Set<Integer> setOtherId = null;

        for (Map.Entry<Integer, User> integerUserEntry : inMemoryUserStorage.userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == id) {
                setId = integerUserEntry.getValue().getFriends();
            } else if (integerUserEntry.getValue().getId() == otherId) {
                setOtherId = integerUserEntry.getValue().getFriends();
            }
        }

        Set<Integer> common = new HashSet<>(setId);
        common.retainAll(setOtherId);

        if (common.size() != 0) {
            for (Map.Entry<Integer, User> integerUserEntry : inMemoryUserStorage.userMap.entrySet()) {
                if (common.contains(integerUserEntry.getValue().getId())) {
                    listCommonFriends.add(integerUserEntry.getValue());
                }
            }
        }
        return listCommonFriends;
    }
}
