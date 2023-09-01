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

    private long timestamp;

    public UserFeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void likeEvent(int userId, int entityId, String operation) {
        event(userId, entityId, operation, "LIKE");
    }

    public void friendEvent(int userId, int entityId, String operation) {
        event(userId, entityId, operation, "FRIEND");
    }

    public void reviewEvent(int userId, int entityId, String operation) {
        event(userId, entityId, operation, "REVIEW");
    }

    private void event(int userId, int entityId, String operation, String eventType) {
        timestamp = Instant.now().getEpochSecond() * 1000 + (long) (Instant.now().getNano() / Math.pow(10, 6));
        jdbcTemplate.update(sqlRequest, userId, entityId, eventType, operation, timestamp);
    }
}
