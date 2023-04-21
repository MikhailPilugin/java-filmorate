package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {

    public User addFriend(long id, long friendId) {
        User user = null;
        User friend;


        //////////////////////////////

        User user1 = InMemoryUserStorage.userList.get((int) id);
        User user2 = InMemoryUserStorage.userList.get((int) friendId);

        User user3 = null;
        User user4 = null;

        for (User user18 : InMemoryUserStorage.userList) {
            if (user18.getId() == id) {
                user3 = user18;
            }
        }

        for (User user21 : InMemoryUserStorage.userList) {
            if (user21.getId() == friendId) {
                user4 = user21;
            }
        }

        System.out.println("user1 " + user1);
        System.out.println("user2 " + user2);
        System.out.println("user3 " + user3);
        System.out.println("user4 " + user4);

        user1.setFriends(friendId);
        user2.setFriends(id);

        System.out.println("user1 " + user1);
        System.out.println("user2 " + user2);
        System.out.println("user3 " + user3);
        System.out.println("user4 " + user4);

        //////////////////////////////

//        if (InMemoryUserStorage.userMap.get(id).getId() == id && InMemoryUserStorage.userMap.get(friendId).getId() == friendId) {
//            for (Map.Entry<Integer, User> integerUserEntry : InMemoryUserStorage.userMap.entrySet()) {
//                if (integerUserEntry.getValue().getId() == id) {
//                    user = integerUserEntry.getValue();
//                    user.setFriends(friendId);
//                    InMemoryUserStorage.userMap.replace(user.getId(), user);
//                }
//            }
//
//            for (Map.Entry<Integer, User> integerUserEntry : InMemoryUserStorage.userMap.entrySet()) {
//                if (integerUserEntry.getValue().getId() == friendId) {
//                    friend = integerUserEntry.getValue();
//                    friend.setFriends(id);
//                    InMemoryUserStorage.userMap.replace(friend.getId(), friend);
//                }
//            }
//        }
        return user;
    }

//    public User delFriend(long id, long friendId) {
//        User user;
//
//        for (Map.Entry<Integer, User> integerUserEntry : InMemoryUserStorage.userMap.entrySet()) {
//            if (integerUserEntry.getValue().getId() == id) {
//                user = integerUserEntry.getValue();
//                Set<Long> friends = user.getFriends();
//                friends.remove(friendId);
//                user.setFriends(friendId);
//                InMemoryUserStorage.userMap.put(user.getId(), user);
//            }
//        }
//
//        for (Map.Entry<Integer, User> integerUserEntry : InMemoryUserStorage.userMap.entrySet()) {
//            if (integerUserEntry.getValue().getId() == friendId) {
//                user = integerUserEntry.getValue();
//                Set<Long> friends = user.getFriends();
//                friends.remove(id);
//                user.setFriends(friendId);
//                InMemoryUserStorage.userMap.put(user.getId(), user);
//            }
//        }
//
//        return InMemoryUserStorage.userMap.get(id);
//    }

    public Set<Long> getFriend(long id) {
        Set<Long> friends = null;

//        for (Map.Entry<Integer, User> integerUserEntry : InMemoryUserStorage.userMap.entrySet()) {
//            if (integerUserEntry.getKey() == id) {
//                friends = integerUserEntry.getValue().getFriends();
//            }
//        }

        return InMemoryUserStorage.userList.get((int) id).getFriends();

//        return friends;
    }

//    public List<Long> getCommonFriends(Long id, Long otherId) {
//        User user;
//
//        if (InMemoryUserStorage.userMap.get(id) != null && InMemoryUserStorage.userMap.get(otherId) != null) {
//            List<Long> friendsList = InMemoryUserStorage.userMap.get(id).getFriends();
//            List<Long> otherFriendsList = InMemoryUserStorage.userMap.get(otherId).getFriends();
//
//            List<Long> commonFriends = findCommonFriends(friendsList, otherFriendsList);
//            return commonFriends;
//        }
//        return Collections.emptySet();
//    }

    private Set<Long> findCommonFriends(Set<Long> first, Set<Long> second) {
        Set<Long> common = new HashSet<>(first);
        common.removeAll(second);
        return common;
    }
}
