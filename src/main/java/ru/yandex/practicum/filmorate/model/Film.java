package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    protected int id;

    @NotNull
    protected String name;

    @Size(max = 200)
    protected String description;
    protected LocalDate releaseDate;
    protected long duration;
}
