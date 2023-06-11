package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Data
public class Review {
    private int reviewId;
    private Integer filmId;
    private Integer userId;
    @NotBlank
    @NonNull
    private String content;
    @NonNull
    private Boolean isPositive;

    private Timestamp date;

    private Integer useful;

    public Review() {
    }

    public Review(int reviewId, int filmId, int userId, String content, Timestamp date, boolean isPositive) {
        this.reviewId = reviewId;
        this.filmId = filmId;
        this.userId = userId;
        this.content = content;
        this.date = date;
        this.isPositive = isPositive;
    }

    public Review(int reviewId, int filmId, int userId, String content, Timestamp date, boolean isPositive, Integer useful) {
        this.reviewId = reviewId;
        this.filmId = filmId;
        this.userId = userId;
        this.content = content;
        this.date = date;
        this.isPositive = isPositive;
        this.useful = useful;
    }
}
