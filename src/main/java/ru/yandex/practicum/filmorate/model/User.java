package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    protected int id;
    protected String email;
    protected String login;
    protected String name;
    protected LocalDate birthday;
}
