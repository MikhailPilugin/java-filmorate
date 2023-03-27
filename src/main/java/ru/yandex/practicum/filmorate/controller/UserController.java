package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    Map<Integer, User> userMap = new HashMap<>();

    @GetMapping
    public Map<Integer, User> getUsers() {
        return userMap;
    }

    @PostMapping
    public void addUser(@RequestBody User user) {
        userMap.put(user.getId(), user);
    }

    @PutMapping
    public void updateUser(@RequestBody User user) {

        if (userMap.containsValue(user.getId())) {
            userMap.put(user.getId(), user);
        }
    }

}
