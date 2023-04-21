package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class User {
    private int id;

    public User() {
        this.friends = new ArrayList<>();
    }

    @Email
    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;

    private List<Integer> friends;

    public void setFriends(Integer id) {
        if (id != null && !friends.contains(id)) {
            friends.add(id);
        }
    }
}
