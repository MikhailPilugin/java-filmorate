package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    protected int id;
    protected String name;
    protected String description;
    protected LocalDate releaseDate;
    protected Duration duration;
}
