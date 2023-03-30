package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    protected int id;

    @NotNull
    @NotBlank
    protected String name;

    @Size(max = 200)
    protected String description;
    protected LocalDate releaseDate;

    @Positive
    protected long duration;
}
