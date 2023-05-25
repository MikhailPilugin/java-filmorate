package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@ResponseBody
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/{reviewId}")
    public Review getReview(@PathVariable Integer reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @PostMapping("/reviews")
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping("/reviews/{reviewId}")
    public Review updateReview(@PathVariable Integer reviewId, @RequestBody Review review) {
        return reviewService.updateReview(reviewId, review);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public boolean deleteReview(@PathVariable Integer reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    @GetMapping("/reviews/film/{filmId}")
    public Collection<Review> getAllReviewsOfFilm(@PathVariable Integer filmId) {
        return reviewService.getAllReviewsOfFilm(filmId);
    }

    @GetMapping("/reviews/user/{userId}")
    public Collection<Review> getAllReviewsOfUser(@PathVariable Integer userId) {
        return reviewService.getAllReviewsOfUser(userId);
    }

    @PostMapping("/reviews/{reviewId}/like/{userId}")
    public boolean likeReview(@PathVariable Integer reviewId, @PathVariable Integer userId){
        return reviewService.likeReview(reviewId, userId, true);
    }


    @PostMapping("/reviews/{reviewId}/dislike/{userId}")
    public boolean dislikeReview(@PathVariable Integer reviewId, @PathVariable Integer userId){
        return reviewService.likeReview(reviewId, userId, false);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInterruptedException(final NullPointerException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ErrorResponse("error", e.getMessage());
    }
}
