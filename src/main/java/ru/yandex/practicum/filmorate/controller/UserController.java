package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
    public void addUser(@RequestBody User user) throws ValidationException {

        // проверка почты
        String email = user.getEmail();
        String commerceAt = "@";
        boolean isEmailContainsAt = email.contains(commerceAt);
        boolean isEmailNotEmpty = !email.isEmpty();

        // проверка логина
        String login = user.getLogin();
        String space = " ";
        boolean isLoginContainsSpace = login.contains(space);
        boolean isLoginNotEmpty = !login.isEmpty();

        // проверка имени - если имя не добавлено, подставляем логин в имя
        String name = user.getName();

        if (name.isEmpty()) {
            user.setName(user.getLogin());
        }

        // проверка даты рождения
        LocalDate today = LocalDate.now();
        boolean isBirthDayBeforeToday = user.getBirthday().isBefore(today);

        // если пользователей ещё нет - добавляем первого
        if (userMap.size() == 0 && isEmailContainsAt && isEmailNotEmpty && !isLoginContainsSpace
        && isLoginNotEmpty && isBirthDayBeforeToday) {
            userMap.put(user.getId(), user);

        // пользователи уже есть
        } else if (isEmailContainsAt && isEmailNotEmpty && !isLoginContainsSpace
                && isLoginNotEmpty && isBirthDayBeforeToday) {

            for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
                if (integerUserEntry.getValue().getId() == user.getId()) {
                    System.out.println("Этот id уже занят");
                } else {
                    userMap.put(user.getId(), user);
                }
            }
        } else {
            throw new ValidationException("Ошибка данных при добавлении пользователя");
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
