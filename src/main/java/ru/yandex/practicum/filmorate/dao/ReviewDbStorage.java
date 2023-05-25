package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Repository
public class ReviewDbStorage {
    private final Logger log = LoggerFactory.getLogger(ReviewDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Review getReview(Integer reviewId) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            Review review = jdbcTemplate.queryForObject(sql, new Object[]{reviewId}, (rs, rowNum) ->
                    new Review(
                            rs.getInt("review_id"),
                            rs.getInt("film_id"),
                            rs.getInt("user_id"),
                            rs.getString("review_text"),
                            rs.getDate("review_date").toLocalDate(),
                            rs.getInt("review_rating")
                    ));

            String sqlLikesDislikes = "SELECT SUM(CASE WHEN is_like = true THEN 1 ELSE 0 END) AS likes, SUM(CASE WHEN is_like = false THEN 1 ELSE 0 END) AS dislikes FROM review_likes WHERE review_id = ?";
            jdbcTemplate.queryForObject(sqlLikesDislikes, new Object[]{reviewId}, (rs, rowNum) -> {
                review.setLikes(rs.getInt("likes"));
                review.setDislikes(rs.getInt("dislikes"));
                return review;
            });

            return review;
        }
        catch(EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Review not found");
        }
        catch (Exception e) {
            log.error("Error getting review by id: {}", reviewId, e);
            throw e;
        }

    }

    public Review addReview(Integer filmId, Integer userId, Review review) {
        log.info("Adding review for filmId: {}, userId: {}", filmId, userId);

        String sql = "INSERT INTO reviews (film_id, user_id, review_text, review_date, review_rating) VALUES (?, ?, ?, NOW(), ?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"review_id"});
                ps.setInt(1, filmId);
                ps.setInt(2, userId);
                ps.setString(3, review.getReview());
                ps.setInt(4, review.getRating());
                return ps;
            }, keyHolder);

            review.setId(keyHolder.getKey().intValue());
            return review;
        }
        catch (Exception e) {
            log.error("Error adding review for filmId: {}, userId: {}", filmId, userId, e);
            throw e;
        }
    }

    public Review updateReview(Integer reviewId, Review review) {
        log.info("Updating review for reviewId: {}", reviewId);
        String sql = "UPDATE reviews SET review_text = ?, review_rating = ? WHERE review_id = ?";

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"review_id"});
                ps.setString(1, review.getReview());
                ps.setInt(2, review.getRating());
                ps.setInt(3, reviewId);
                return ps;
            }, keyHolder);

            review.setId(keyHolder.getKey().intValue());
            return review;
        }
        catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Review with id " + reviewId + " not found");
        }
        catch (Exception e) {
            log.error("Error updating review by id: {}", reviewId, e);
            throw e;
        }
    }

    public boolean deleteReview(Integer reviewId) {
        log.info("Deleting review for reviewId: {}", reviewId);
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
        return true;
    }

    public List<Review> getAllReviewsOfFilm(Integer filmId) {
        String sql = "SELECT r.*, SUM(CASE WHEN is_like = true THEN 1 ELSE 0 END) AS likes, SUM(CASE WHEN is_like = false THEN 1 ELSE 0 END) AS dislikes FROM reviews r LEFT JOIN review_likes rl ON r.review_id = rl.review_id WHERE r.film_id = ? GROUP BY r.review_id";


        try {
            return jdbcTemplate.query(sql, new Object[]{filmId}, (rs, rowNum) ->
                    new Review(
                            rs.getInt("review_id"),
                            rs.getInt("film_id"),
                            rs.getInt("user_id"),
                            rs.getString("review_text"),
                            rs.getInt("review_rating"),
                            rs.getDate("review_date").toLocalDate(),
                            rs.getInt("likes"),
                            rs.getInt("dislikes")
                    ));
        }
        catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Film with id " + filmId + " not found");
        }
        catch (Exception e) {
            log.error("Error getting reviews by film id: {}", filmId, e);
            throw e;
        }
    }

    public List<Review> getAllReviewsOfUser(Integer userId) {
        String sql = "SELECT r.*, SUM(CASE WHEN is_like = true THEN 1 ELSE 0 END) AS likes, SUM(CASE WHEN is_like = false THEN 1 ELSE 0 END) AS dislikes FROM reviews r LEFT JOIN review_likes rl ON r.review_id = rl.review_id WHERE r.user_id = ? GROUP BY r.review_id";

        try {
            return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) ->
                    new Review(
                            rs.getInt("review_id"),
                            rs.getInt("film_id"),
                            rs.getInt("user_id"),
                            rs.getString("review_text"),
                            rs.getInt("review_rating"),
                            rs.getDate("review_date").toLocalDate(),
                            rs.getInt("likes"),
                            rs.getInt("dislikes")
                    ));
        }
        catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("User with id " + userId + " not found");
        }
        catch (Exception e) {
            log.error("Error getting reviews by user id: {}", userId, e);
            throw e;
        }
    }

    public boolean likeReview(Integer reviewId, Integer userId, Boolean isLike){
        if(isLike)
            log.info("Adding like for reviewId: {}, userId: {}", reviewId, userId);
        else
            log.info("Adding dislike for reviewId: {}, userId: {}", reviewId, userId);
        String sql = "MERGE INTO review_likes KEY(review_id, user_id) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, reviewId, userId, isLike);
            return true;
        }
        catch (Exception e) {
            log.error("Error adding like for reviewId: {}, userId: {}", reviewId, userId, e);
            throw e;
        }
    }
}
