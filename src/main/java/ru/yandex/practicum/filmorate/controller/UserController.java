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

        if (userMap.size() == 0) {
            userMap.put(user.getId(), user);

        } else {
            for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
                if (integerUserEntry.getValue().getId() == user.getId()) {
                    System.out.println("Этот id уже занят");
                } else {
                    userMap.put(user.getId(), user);
                }
            }
        }
    }

    @PutMapping
    public void updateUser(@RequestBody User user) {

        for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == user.getId()) {
                userMap.replace(user.getId(), user);
            }
        }
    }
}
