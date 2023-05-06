package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
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

    private String genre;
    private String mpaRating;

    public Film() {
        this.likes = new HashSet<>();
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

    public int removeLikes(Integer id) {
        int status = 0;

        if (id != null) {
            likes.remove(id);
            status = 1;
            rate--;
        }
        return status;
    }

}
