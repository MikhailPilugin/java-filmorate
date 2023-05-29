package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, User> getUsers() {
        Map<Integer, User> userMap = new HashMap<>();

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users");

        if (userRows.next()) {
            do {
                User user = new User();

                user.setId(Integer.valueOf(userRows.getString("user_id")));
                user.setLogin(userRows.getString("login"));
                user.setName(userRows.getString("user_name"));
                user.setBirthday(LocalDate.parse(userRows.getString("birthday")));
                user.setEmail(userRows.getString("email"));

                userMap.put(user.getId(), user);
            } while (userRows.next());
        }

        return userMap;
    }

    @Override
    public User getUserById(Integer id) throws IllegalArgumentException {
        User user = new User();

        SqlRowSet userRowsNotFound = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        if (!userRowsNotFound.next()) {
            throw new IllegalArgumentException("user not found");
        }

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        if (userRows.next()) {
            user.setId(Integer.valueOf(userRows.getString("user_id")));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("user_name"));
            user.setBirthday(LocalDate.parse(userRows.getString("birthday")));
            user.setEmail(userRows.getString("email"));
        }
        return user;
    }

    @Override
    public User addUser(User user) throws ValidationException {
        User newUser = new User();
        LocalDate localDate = user.getBirthday();

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("login contains spaces");
        }

        if (!localDate.isBefore(LocalDate.now())) {
            throw new ValidationException("date of birthday is wrong");
        }

        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        String sqlQuery = "insert into users(login, user_name, birthday, email) " +
                "values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, String.valueOf(user.getBirthday()));
            stmt.setString(4, user.getEmail());
            return stmt;
        }, keyHolder);

        newUser.setId(keyHolder.getKey().intValue());
        newUser.setLogin(user.getLogin());
        newUser.setName(user.getName());
        newUser.setBirthday(user.getBirthday());
        newUser.setEmail(user.getEmail());

        return newUser;
    }

    @Override
    public User updateUser(User user) throws IllegalArgumentException {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", user.getId());

        if (!userRows.next()) {
            throw new IllegalArgumentException("user not found");
        }

        String sqlQuery = "update users set " +
                "login = ?, user_name = ?, birthday = ?, email = ? " +
                "where user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getEmail(),
                user.getId());

        return user;
    }

    @Override
    public User deleteUser(User user) throws IllegalArgumentException {
        SqlRowSet userRowsNotFound = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", user.getId());

        if (!userRowsNotFound.next()) {
            throw new IllegalArgumentException("user not found");
        }

        String sqlQueryOne = "delete from users where user_id = ?";

        int status = jdbcTemplate.update(sqlQueryOne, user.getId());

        return user;
    }

    @Override
    public User deleteUserById(Integer id) throws IllegalArgumentException {
        User user = new User();

        SqlRowSet userRowsNotFound = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        if (!userRowsNotFound.next()) {
            throw new IllegalArgumentException("user not found");
        }

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        if (userRows.next()) {
            user.setId(Integer.valueOf(userRows.getString("user_id")));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("user_name"));
            user.setBirthday(LocalDate.parse(userRows.getString("birthday")));
            user.setEmail(userRows.getString("email"));
        }

        String sqlQueryTwo = "delete from friendship where user_id = ?";
        String sqlQueryThree = "delete from friendship where friend_id = ?";

        jdbcTemplate.update(sqlQueryTwo, id);
        jdbcTemplate.update(sqlQueryThree, id);

        String sqlQueryOne = "delete from users where user_id = ?";

        jdbcTemplate.update(sqlQueryOne, id);

        return user;
    }

    public Collection<Feed> getUserFeed(int userId) {
        return jdbcTemplate.query("SELECT * FROM user_feed WHERE user_id = ?", this::mapRowToFeed, userId);
    }

    private Feed mapRowToFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .userId(rs.getInt("user_id"))
                .eventId(rs.getInt("event_id"))
                .entityId(rs.getInt("entity_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .timestamp(rs.getInt("timestamp"))
                .build();
    }

}
