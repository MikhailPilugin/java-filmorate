package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class User {
    private int id;

    public User() {
        this.friends = new HashSet<>();
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

    private Set<Integer> friends;

    public int setFriends(Integer id) {
        int status = 1;

        if (id != null) {
            friends.add(id);
            status = 0;
        }
        return status;
    }

    public int removeFriends(Integer id) {
        int status = 1;

        if (id != null) {
            friends.remove(id);
            status = 0;
        }
        return status;
    }
}
