package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {
    public Map<Integer, User> userMap;

    @Autowired
    public UserService(Map<Integer, User> userMap) {
        this.userMap = userMap;
    }

    public User addFriend(long id, long otherId) {
        User user;

        for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == id) {
                user = integerUserEntry.getValue();
                Set<Long> friends = user.getFriends();
                friends.add(otherId);
                user.setFriends(friends);
                userMap.put(user.getId(), user);
            }
        }

        for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == otherId) {
                user = integerUserEntry.getValue();
                Set<Long> friends = user.getFriends();
                friends.add(id);
                user.setFriends(friends);
                userMap.put(user.getId(), user);
            }
        }

        return userMap.get(id);
    }

    public User delFriend(long id, long otherId) {
        User user;

        for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == id) {
                user = integerUserEntry.getValue();
                Set<Long> friends = user.getFriends();
                friends.remove(otherId);
                user.setFriends(friends);
                userMap.put(user.getId(), user);
            }
        }

        for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == otherId) {
                user = integerUserEntry.getValue();
                Set<Long> friends = user.getFriends();
                friends.remove(id);
                user.setFriends(friends);
                userMap.put(user.getId(), user);
            }
        }

        return userMap.get(id);
    }

    public Set<Long> getFriends(long id) {
        Set<Long> friends = userMap.get(id).getFriends();
        return friends;
    }

    public Set<Long> getCommonFriends(long id, long otherId) {
        User user;
        Set<Long> friendsList = userMap.get(id).getFriends();
        Set<Long> otherFriendsList = userMap.get(otherId).getFriends();

        Set<Long> commonFriends = findCommonFriends(friendsList, otherFriendsList);

        return commonFriends;
    }

    private Set<Long> findCommonFriends(Set<Long> first, Set<Long> second) {
        Set<Long> common = new HashSet<>(first);
        common.removeAll(second);
        return common;
    }
}
