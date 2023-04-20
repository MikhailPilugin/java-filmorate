package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {
    public Map<Integer, User> userMap;
    public InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(Map<Integer, User> userMap) {
        this.userMap = userMap;
    }

    public User addFriend(long id, long friendId) {
        User user;

        if (userMap.containsKey(id) && userMap.containsKey(friendId)) {
            for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
                if (integerUserEntry.getValue().getId() == id) {
                    user = integerUserEntry.getValue();
                    Set<Long> friends = user.getFriends();
                    friends.add(friendId);
                    user.setFriends(friends);
                    userMap.put(user.getId(), user);
                }
            }

            for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
                if (integerUserEntry.getValue().getId() == friendId) {
                    user = integerUserEntry.getValue();
                    Set<Long> friends = user.getFriends();
                    friends.add(id);
                    user.setFriends(friends);
                    userMap.put(user.getId(), user);
                }
            }
        }
        return userMap.get(id);
    }

    public User delFriend(long id, long friendId) {
        User user;

        for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == id) {
                user = integerUserEntry.getValue();
                Set<Long> friends = user.getFriends();
                friends.remove(friendId);
                user.setFriends(friends);
                userMap.put(user.getId(), user);
            }
        }

        for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == friendId) {
                user = integerUserEntry.getValue();
                Set<Long> friends = user.getFriends();
                friends.remove(id);
                user.setFriends(friends);
                userMap.put(user.getId(), user);
            }
        }

        return userMap.get(id);
    }

    public Set<Long> getFriends(Long id) {
        Set<Long> friends = userMap.get(id).getFriends();
        return friends;
    }

    public Set<Long> getCommonFriends(Long id, Long otherId) {
        User user;

        if (userMap.get(id) != null && userMap.get(otherId) != null) {
            Set<Long> friendsList = userMap.get(id).getFriends();
            Set<Long> otherFriendsList = userMap.get(otherId).getFriends();

            Set<Long> commonFriends = findCommonFriends(friendsList, otherFriendsList);
            return commonFriends;
        }
        return Collections.emptySet();
    }

    private Set<Long> findCommonFriends(Set<Long> first, Set<Long> second) {
        Set<Long> common = new HashSet<>(first);
        common.removeAll(second);
        return common;
    }
}
