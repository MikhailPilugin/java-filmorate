package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Set;

@Service
public class UserService {
    public Map<Integer, User> userMap;

    @Autowired
    public UserService(Map<Integer, User> userMap) {
        this.userMap = userMap;
    }

    public User addFriend(long id, long friendId) {
        User user;

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

    public Set<Long> getMutualFriends(long id) {
        return userMap.get(id).getFriends();
    }
}
