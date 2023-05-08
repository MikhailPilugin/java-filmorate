package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, User> getUsers() {
        Map<Integer, User> userMap = new HashMap<>();

        // выполняем запрос к базе данных.
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users");

        // обрабатываем результат выполнения запроса
            if(userRows.next()) {
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
    public User getUserById(Integer id) {
        User user = new User();

        SqlRowSet userRowsNotFound = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        if(!userRowsNotFound.next()) {
            throw new IllegalArgumentException("");
        }

        // выполняем запрос к базе данных.
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        // обрабатываем результат выполнения запроса
        if(userRows.next()) {
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
    public User updateUser(User user) throws ValidationException {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", user.getId());

        if(!userRows.next()) {
            throw new IllegalArgumentException("");
        }

        String sqlQuery = "update users set " +
                    "login = ?, user_name = ?, birthday = ?, email = ? " +
                    "where user_id = ?";
            jdbcTemplate.update(sqlQuery
                    , user.getLogin()
                    , user.getName()
                    , user.getBirthday()
                    , user.getEmail()
                    , user.getId());

        return user;
    }

    @Override
    public User deleteUser(User user) throws ValidationException {
        SqlRowSet userRowsNotFound = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", user.getId());

        if(!userRowsNotFound.next()) {
            throw new IllegalArgumentException("");
        }

        String sqlQueryOne = "delete from users where user_id = ?";

        int status = jdbcTemplate.update(sqlQueryOne, user.getId());

        return user;
    }
}
