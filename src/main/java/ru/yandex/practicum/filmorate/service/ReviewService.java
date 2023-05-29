package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review getReviewById(Integer reviewId);

    Review addReview(Review review);

    Review updateReview(Integer reviewId, Review review);

    boolean deleteReview(Integer reviewId);

    List<Review> getAllReviewsOfFilm(Integer filmId, Integer count);

    List<Review> getAllReviewsOfUser(Integer userId);

    List<Review> getAllReviews(Integer count);

    Review likeReview(Integer reviewId, Integer userId);

    Review dislikeReview(Integer reviewId, Integer userId);

}
