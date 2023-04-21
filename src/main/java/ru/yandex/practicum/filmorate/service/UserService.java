package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    public void addFriend(long id, long friendId) {
        List<User> testList = InMemoryUserStorage.userList;

        User user = null;
        User userFriend = null;
        int indexUser = 0;
        int indexUserFriend = 0;

        for (int i = 0; i < InMemoryUserStorage.userList.size(); i++) {
            if (InMemoryUserStorage.userList.get(i).getId() == id) {
                user = InMemoryUserStorage.userList.get(i);
                indexUser = InMemoryUserStorage.userList.get(i).getId();
            }
        }

        for (int i = 0; i < InMemoryUserStorage.userList.size(); i++) {
            if (InMemoryUserStorage.userList.get(i).getId() == friendId) {
                userFriend = InMemoryUserStorage.userList.get(i);
                indexUserFriend = InMemoryUserStorage.userList.get(i).getId();
            }
        }

        System.out.println("user1 " + user);
        System.out.println("userFriend1 " + userFriend);

        user.setFriends(indexUserFriend);
        userFriend.setFriends(indexUser);

        testList.add(user);
        testList.add(userFriend);

        System.out.println(testList);

        InMemoryUserStorage.userList.set(indexUser, user);
        InMemoryUserStorage.userList.set(indexUserFriend, userFriend);


        System.out.println("user2 " + user);
        System.out.println("userFriend2 " + userFriend);

//
//        user1.setFriends(friendId);
//        user2.setFriends(id);
//
//        System.out.println("user1 " + user1);
//        System.out.println("user2 " + user2);
//        System.out.println("user3 " + user3);
//        System.out.println("user4 " + user4);

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
//        return user;
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

    public List<Integer> getFriend(int id) {
        List<Integer> friends = null;

//        for (Map.Entry<Integer, User> integerUserEntry : InMemoryUserStorage.userMap.entrySet()) {
//            if (integerUserEntry.getKey() == id) {
//                friends = integerUserEntry.getValue().getFriends();
//            }
//        }

        return InMemoryUserStorage.userList.get(id).getFriends();

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
