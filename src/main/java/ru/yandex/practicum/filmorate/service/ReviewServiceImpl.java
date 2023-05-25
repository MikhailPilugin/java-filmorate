package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDbStorage reviewDbStorage;

    public ReviewServiceImpl(ReviewDbStorage reviewDbStorage) {
        this.reviewDbStorage = reviewDbStorage;
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        return reviewDbStorage.getReview(reviewId);
    }

    @Override
    public Review addReview(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        return reviewDbStorage.addReview(review.getFilmId(), review.getUserId(), review);
    }

    @Override
    public Review updateReview(Integer reviewId, Review review) {
        return reviewDbStorage.updateReview(reviewId, review);
    }

    @Override
    public boolean deleteReview(Integer reviewId) {
        return reviewDbStorage.deleteReview(reviewId);
    }

    @Override
    public List<Review> getAllReviewsOfFilm(Integer filmId) {
        return reviewDbStorage.getAllReviewsOfFilm(filmId);
    }

    @Override
    public List<Review> getAllReviewsOfUser(Integer userId) {
        return reviewDbStorage.getAllReviewsOfUser(userId);
    }

    @Override
    public boolean likeReview(Integer reviewId, Integer userId, Boolean isLike) {
        return reviewDbStorage.likeReview(reviewId, userId, isLike);
    }
}
