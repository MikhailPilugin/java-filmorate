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
    public List<Review> getAllReviews(Integer count) {
        return reviewDbStorage.getAllReviews(count);
    }

    @Override
    public Review addReview(Review review) {
        if(review.getFilmId() == null || review.getUserId() == null) {
            throw new NullPointerException("FilmId and userId must be not null");
        }
        if (review.getFilmId() < 1 || review.getUserId() < 1) {
            throw new IllegalArgumentException("FilmId and userId must be greater than 0");
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
    public List<Review> getAllReviewsOfFilm(Integer filmId, Integer count) {
        return reviewDbStorage.getAllReviewsOfFilm(filmId, count);
    }

    @Override
    public List<Review> getAllReviewsOfUser(Integer userId) {
        return reviewDbStorage.getAllReviewsOfUser(userId);
    }

    @Override
    public Review likeReview(Integer reviewId, Integer userId) {
        return reviewDbStorage.likeReview(reviewId, userId, true);
    }

    @Override
    public Review dislikeReview(Integer reviewId, Integer userId) {
        return reviewDbStorage.dislikeReview(reviewId, userId, false);
    }

}
