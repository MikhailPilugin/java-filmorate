package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Mpa {
    private int id;
    private String name;

    public Mpa() {
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Mpa(@JsonProperty("id") Integer id,
               @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
