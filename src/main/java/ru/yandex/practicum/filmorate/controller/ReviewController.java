package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@ResponseBody
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews")
    public Collection<Review> getReviews(@RequestParam(required = false) Integer filmId, @RequestParam(required = false, defaultValue = "10") Integer count) {
        return reviewService.getAllReviewsOfFilm(filmId, count);
    }

    @GetMapping("/reviews/{reviewId}")
    public Review getReview(@PathVariable Integer reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @PostMapping("/reviews")
    public Review addReview(@RequestBody @Valid Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping("/reviews")
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review.getReviewId(), review);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public boolean deleteReview(@PathVariable Integer reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    @GetMapping("/reviews/user/{userId}")
    public Collection<Review> getAllReviewsOfUser(@PathVariable Integer userId) {
        return reviewService.getAllReviewsOfUser(userId);
    }

    @PutMapping("/reviews/{reviewId}/like/{userId}")
    public Review likeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        return reviewService.likeReview(reviewId, userId);
    }

    @PutMapping("/reviews/{reviewId}/dislike/{userId}")
    public Review dislikeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        return reviewService.dislikeReview(reviewId, userId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ErrorResponse("Аргументы не прошли валидацию", e.getMessage());
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
}
