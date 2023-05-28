package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private int id;

    @NotNull
    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;

    @Positive
    private long duration;

    private Set<Integer> likes;
    private int rate;

    private Mpa mpa;
    private List<Genres> genres;

    List<Director> directors;

    public Film() {
        this.likes = new HashSet<>();
        this.genres = new ArrayList<>();
        this.directors = new ArrayList<>();
    }


    public Film(int id, String name, String description, LocalDate releaseDate,
                long duration, Set<Integer> likes, int rate, Mpa mpa, List<Genres> genres,
                List<Director> directors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        if (likes == null) {
            this.likes = new HashSet<>();
        } else {
            this.likes = likes;
        }
        this.rate = rate;
        this.mpa = mpa;
        if (genres == null) {
            this.genres = new ArrayList<>();
        } else {
            this.genres = genres;
        }
        if (genres == null) {
            this.directors = new ArrayList<>();
        } else {
            this.directors = directors;
        }
    }

    public int setLikes(Integer id) {
        int status = 0;

        if (id != null) {
            likes.add(id);
            status = 1;
            rate++;
        }
        return status;
    }


}
