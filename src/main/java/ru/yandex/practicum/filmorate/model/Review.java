package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class Review {
    private int id;
    @NonNull
    private int filmId;
    @NonNull
    private int userId;
    @NotBlank
    private String review;
    @NonNull
    private int rating;
    private LocalDate date;
    private int likes;
    private int dislikes;

    public Review() {
    }

    public Review(int id, int filmId, int userId, String review, LocalDate date, int rating) {
        this.id = id;
        this.filmId = filmId;
        this.userId = userId;
        this.review = review;
        this.date = date;
        this.rating = rating;
    }

    public Review(int id, int filmId, int userId, String review, int rating, LocalDate date, int likes, int dislikes) {
        this.id = id;
        this.filmId = filmId;
        this.userId = userId;
        this.review = review;
        this.date = date;
        this.rating = rating;
        this.likes = likes;
        this.dislikes = dislikes;
    }
}
