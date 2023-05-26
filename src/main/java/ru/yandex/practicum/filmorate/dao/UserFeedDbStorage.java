package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.UserFeedStorage;

import java.time.Instant;

@Repository
public class UserFeedDbStorage implements UserFeedStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String sqlRequest = "INSERT INTO user_feed(user_id, entity_id, event_type, operation, timestamp) "
            + "values(?, ?, ?, ?, ?)";

    public UserFeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void likeEvent(int user_id, int entity_id, String operation) {
        jdbcTemplate.update(sqlRequest, user_id, entity_id, "LIKE", operation, Instant.now().getEpochSecond());
    }

    public void friendEvent(int user_id, int entity_id, String operation) {
        jdbcTemplate.update(sqlRequest, user_id, entity_id, "FRIEND", operation, Instant.now().getEpochSecond());
    }

    public void reviewEvent(int user_id, int entity_id, String operation) {
        jdbcTemplate.update(sqlRequest, user_id, entity_id, "REVIEW", operation, Instant.now().getEpochSecond());
    }
}
