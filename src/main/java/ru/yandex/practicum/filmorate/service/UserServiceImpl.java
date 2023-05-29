package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.UserFeedDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserFeedStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDbStorage userDbStorage;
    private final UserFeedStorage userFeedStorage;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addFriend(int id, int friendId) {
        SqlRowSet userRowsOne = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);
        SqlRowSet userRowsTwo = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", friendId);

        boolean isFriendsAdd = false;

        if (!userRowsOne.next()) {
            throw new IllegalArgumentException("");
        }

        if (!userRowsTwo.next()) {
            throw new IllegalArgumentException("");
        }

        String sqlQueryOne = "insert into friendship (user_id, friend_id, status) " +
                "values (?, ?, ?)";

        int statusOne = jdbcTemplate.update(sqlQueryOne,
                id,
                friendId,
                "CONFIRMED");

        if (statusOne > 0) {
            isFriendsAdd = true;
        }

        userFeedStorage.friendEvent(id, friendId, "ADD");

        return isFriendsAdd;
    }

    @Override
    public boolean delFriend(Integer id, Integer friendId) {
        SqlRowSet userRowsOne = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);
        SqlRowSet userRowsTwo = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", friendId);

        boolean isFriendsDelete = false;

        if (!userRowsOne.next()) {
            throw new IllegalArgumentException("");
        }

        if (!userRowsTwo.next()) {
            throw new IllegalArgumentException("");
        }

        String sqlQueryOne = "delete from friendship where user_id = ? and friend_id = ?";

        int status = jdbcTemplate.update(sqlQueryOne, id, friendId);

        if (status > 0) {
            isFriendsDelete = true;
        }

        userFeedStorage.friendEvent(id, friendId, "REMOVE");

        return isFriendsDelete;
    }

    @Override
    public Collection<User> getFriend(int id) {
        List<User> userFriendsList = new ArrayList<>();
        Set<Integer> userFriendsSet = new HashSet<>();

        SqlRowSet userRowsNotFound = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        if (!userRowsNotFound.next()) {
            throw new IllegalArgumentException("");
        }

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where user_id = ?", id);

        if (userRows.next()) {
            do {
                userFriendsSet.add(Integer.valueOf(userRows.getString("friend_id")));
            } while (userRows.next());
        }

        for (Integer friendId : userFriendsSet) {
            SqlRowSet friendRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", friendId);

            if (friendRows.next()) {

                User user = new User();

                user.setId(Integer.valueOf(friendRows.getString("user_id")));
                user.setLogin(friendRows.getString("login"));
                user.setName(friendRows.getString("user_name"));
                user.setBirthday(LocalDate.parse(friendRows.getString("birthday")));
                user.setEmail(friendRows.getString("email"));

                userFriendsList.add(user);
            }
        }

        return userFriendsList;
    }

    @Override
    public Collection<User> getCommonFriends(Integer id, Integer otherId) {

        List<User> userFriendsList = new ArrayList<>();
        Set<Integer> userFriendsSet = new HashSet<>();

        SqlRowSet userRowsNotFound = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        if (!userRowsNotFound.next()) {
            throw new IllegalArgumentException("");
        }

        userRowsNotFound = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", otherId);

        if (!userRowsNotFound.next()) {
            throw new IllegalArgumentException("");
        }

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM friendship WHERE friend_id IN (" +
                " SELECT friend_id FROM friendship WHERE user_id = ? INTERSECT " +
                "SELECT friend_id FROM friendship WHERE user_id = ? )", id, otherId);

        if (userRows.next()) {
            do {
                userFriendsSet.add(Integer.valueOf(userRows.getString("friend_id")));
            } while (userRows.next());
        }

        for (Integer friendId : userFriendsSet) {
            SqlRowSet friendRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", friendId);

            if (friendRows.next()) {

                User user = new User();

                user.setId(Integer.valueOf(friendRows.getString("user_id")));
                user.setLogin(friendRows.getString("login"));
                user.setName(friendRows.getString("user_name"));
                user.setBirthday(LocalDate.parse(friendRows.getString("birthday")));
                user.setEmail(friendRows.getString("email"));

                userFriendsList.add(user);
            }
        }

        return userFriendsList;
    }
}
