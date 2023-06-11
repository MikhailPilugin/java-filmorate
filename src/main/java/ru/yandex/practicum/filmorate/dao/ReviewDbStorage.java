package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.UserFeedStorage;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class ReviewDbStorage {
    private final Logger log = LoggerFactory.getLogger(ReviewDbStorage.class);

    private final JdbcTemplate jdbcTemplate;
    private final UserFeedStorage userFeedStorage;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, UserFeedStorage userFeedStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userFeedStorage = userFeedStorage;
    }

    public Review getReview(Integer reviewId) {
        String sql = "SELECT r.*, SUM( CASE WHEN  rl.is_useful IS NULL "
                + " THEN  0 ELSE CASE WHEN  rl.is_useful THEN  1 ELSE -1 END END) AS count FROM reviews r LEFT JOIN review_likes rl ON r.review_id = rl.review_id WHERE r.review_id = ? GROUP BY r.review_id";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{reviewId}, (rs, rowNum) ->
                    new Review(
                            rs.getInt("review_id"),
                            rs.getInt("film_id"),
                            rs.getInt("user_id"),
                            rs.getString("review_text"),
                            rs.getTimestamp("review_date"),
                            rs.getBoolean("review_is_positive"),
                            rs.getInt("count")
                    ));
        } catch (EmptyResultDataAccessException | NullPointerException e) {
            log.info("Review with id {} not found", reviewId);
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Review not found");
        } catch (Exception e) {
            log.error("Error getting review with id {}", reviewId, e);
            throw e;
        }
    }

    public List<Review> getAllReviews(Integer count) {
        String sql = "SELECT r.*, SUM( CASE WHEN  rl.is_useful IS NULL "
                + " THEN  0 ELSE CASE WHEN  rl.is_useful THEN  1 ELSE -1 END END) AS count FROM reviews r LEFT JOIN review_likes rl ON r.review_id = rl.review_id GROUP BY r.review_id ORDER BY count DESC LIMIT ?";
        try {
            return jdbcTemplate.query(sql, new Object[]{count}, (rs, rowNum) ->
                    new Review(
                            rs.getInt("review_id"),
                            rs.getInt("film_id"),
                            rs.getInt("user_id"),
                            rs.getString("review_text"),
                            rs.getTimestamp("review_date"),
                            rs.getBoolean("review_is_positive"),
                            rs.getInt("count")
                    ));
        } catch (Exception e) {
            log.error("Error getting all reviews", e);
            throw e;
        }
    }

    public Review addReview(Integer filmId, Integer userId, Review review) {
        log.info("Adding review for filmId: {}, userId: {}", filmId, userId);

        String sql = "INSERT INTO reviews (film_id, user_id, review_text, review_date, review_is_positive) VALUES (?, ?, ?, NOW(), ?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"review_id", "review_date"});
                ps.setInt(1, filmId);
                ps.setInt(2, userId);
                ps.setString(3, review.getContent());
                ps.setBoolean(4, review.getIsPositive());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            Timestamp reviewDate = (Timestamp) keys.get("review_date");
            Integer reviewId = (Integer) keys.get("review_id");

            userFeedStorage.reviewEvent(userId, reviewId, "ADD");

            return new Review(reviewId, filmId, userId, review.getContent(), reviewDate, review.getIsPositive(), 0);
        } catch (Exception e) {
            log.error("Error adding review for filmId: {}, userId: {}", filmId, userId, e);
            throw e;
        }
    }

    public Review updateReview(Integer reviewId, Review review) {
        log.info("Updating review for reviewId: {}", reviewId);
        String sql = "UPDATE reviews SET review_text = ?, review_is_positive = ?, review_date = NOW() WHERE review_id = ?";
        String sqlUserId = "SELECT user_id FROM reviews WHERE review_id = ?";
        int userId = jdbcTemplate.queryForObject(sqlUserId, Integer.class, reviewId);
        try {
            userFeedStorage.reviewEvent(userId, reviewId, "UPDATE");
            jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), reviewId);
            return getReview(reviewId);
        } catch (Exception e) {
            log.error("Error updating review for reviewId: {}", reviewId, e);
            throw e;
        }
    }

    public boolean deleteReview(Integer reviewId) {
        log.info("Deleting review for reviewId: {}", reviewId);
        String sqlReviewLikes = "DELETE FROM review_likes WHERE review_id = ?";
        String sqlReview = "DELETE FROM reviews WHERE review_id = ?";
        String sqlUserId = "SELECT user_id FROM reviews WHERE review_id = ?";
        int userId = jdbcTemplate.queryForObject(sqlUserId, Integer.class, reviewId);

        try {
            jdbcTemplate.update(sqlReviewLikes, reviewId);
            jdbcTemplate.update(sqlReview, reviewId);

            userFeedStorage.reviewEvent(userId, reviewId, "REMOVE");
        } catch (Exception e) {
            log.error("Error deleting review for reviewId: {}", reviewId, e);
            throw e;
        }
        return true;
    }

    public List<Review> getAllReviewsOfFilm(Integer filmId, Integer count) {
        String sql = "SELECT r.*, SUM( CASE WHEN  rl.is_useful IS NULL "
                + " THEN  0 ELSE CASE WHEN  rl.is_useful THEN  1 ELSE -1 END END) AS count FROM reviews r LEFT JOIN review_likes rl ON r.review_id = rl.review_id WHERE r.film_id = ? GROUP BY r.review_id ORDER BY count DESC LIMIT ?";
        try {
            return jdbcTemplate.query(sql, new Object[]{filmId, count}, (rs, rowNum) ->
                    new Review(
                            rs.getInt("review_id"),
                            rs.getInt("film_id"),
                            rs.getInt("user_id"),
                            rs.getString("review_text"),
                            rs.getTimestamp("review_date"),
                            rs.getBoolean("review_is_positive"),
                            rs.getInt("count")
                    ));
        } catch (EmptyResultDataAccessException | NullPointerException e) {
            log.info("Film with id {} not found", filmId);
            throw new IllegalArgumentException("Film with id " + filmId + " not found");
        } catch (Exception e) {
            log.error("Error getting reviews by film id: {}", filmId, e);
            throw e;
        }
    }

    public List<Review> getAllReviewsOfUser(Integer userId) {
        String sql = "SELECT * FROM reviews WHERE user_id = ?";

        try {
            return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) ->
                    new Review(
                            rs.getInt("review_id"),
                            rs.getInt("film_id"),
                            rs.getInt("user_id"),
                            rs.getString("review_text"),
                            rs.getTimestamp("review_date"),
                            rs.getBoolean("review_is_positive")
                    ));
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("User with id " + userId + " not found");
        } catch (Exception e) {
            log.error("Error getting reviews by user id: {}", userId, e);
            throw e;
        }
    }

    public Review likeReview(Integer reviewId, Integer userId, Boolean isUseful) {
        log.info("Liking review for reviewId: {}, userId: {}", reviewId, userId);
        String sql = "INSERT INTO review_likes (review_id, user_id, is_useful) VALUES (?, ?, ?)";

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"review_id"});
                ps.setInt(1, reviewId);
                ps.setInt(2, userId);
                ps.setBoolean(3, isUseful);
                return ps;
            }, keyHolder);
            Review review = getReview(reviewId);
            review.setUseful(review.getUseful() + 1);
            return review;
        } catch (Exception e) {
            log.error("Error liking review by id: {}", reviewId, e);
            throw e;
        }
    }

    public Review dislikeReview(Integer reviewId, Integer userId, Boolean isUseful) {
        log.info("Disliking review for reviewId: {}, userId: {}", reviewId, userId);
        String sql = "INSERT INTO review_likes (review_id, user_id, is_useful) VALUES (?, ?, ?)";

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"review_id"});
                ps.setInt(1, reviewId);
                ps.setInt(2, userId);
                ps.setBoolean(3, isUseful);
                return ps;
            }, keyHolder);
            Review review = getReview(reviewId);
            review.setUseful(review.getUseful() - 1);
            return review;
        } catch (Exception e) {
            log.error("Error disliking review by id: {}", reviewId, e);
            throw e;
        }
    }
}
