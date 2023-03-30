package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    
    Map<Integer, User> userMap = new HashMap<>();
    int id = 1;

    @GetMapping
    public Collection<?> getUsers() {
        return userMap.values();
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) throws ValidationException {

        // проверка логина
        String login = user.getLogin();
        String space = " ";
        boolean isLoginContainsSpace = login.contains(space);
        boolean isLoginNotEmpty = !login.isEmpty();

        // проверка имени - если имя не добавлено, подставляем логин в имя
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        // проверка даты рождения
        LocalDate today = LocalDate.now();
        boolean isBirthDayBeforeToday = user.getBirthday().isBefore(today);

        // если пользователей ещё нет - добавляем первого
        if (userMap.size() == 0 && !isLoginContainsSpace && isLoginNotEmpty && isBirthDayBeforeToday) {
            user.setId(id);
            userMap.put(id, user);

            log.info("Добавлен новый пользователь: " + user.getLogin());

        // пользователи уже есть
        } else if (isLoginNotEmpty && isBirthDayBeforeToday && !isLoginContainsSpace) {

            for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
                String userLogin = integerUserEntry.getValue().getLogin();

                if (user.getLogin().equals(userLogin)) {
                    log.info("Попытка добавления пользователя с занятым логином: " + user.getLogin());
                    throw new ValidationException("Этот login уже занят");
                }
            }

            user.setId(++id);

            userMap.put(user.getId(), user);
            log.info("Добавлен новый пользователь: " + user.getLogin());

        } else {
            log.info("Ошибка данных при добавлении пользователя");
            throw new ValidationException("Ошибка данных при добавлении пользователя");
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) throws ValidationException {

        int userId = user.getId();

        for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == userId) {
                userMap.put(userId, user);
                log.info("Данные пользователя обновлены: " + user.getLogin());
            } else {
                log.info("Попытка обновления данных несуществующего пользователя");
                throw new ValidationException("Попытка обновления данных несуществующего пользователя");
            }
        }
        return user;
    }
}
