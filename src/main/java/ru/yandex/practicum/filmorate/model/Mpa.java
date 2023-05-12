package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Mpa {
    private int id;
    private String name;

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
