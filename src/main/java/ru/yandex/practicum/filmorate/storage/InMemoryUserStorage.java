package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    public Map<Integer, User> userMap = new HashMap<>();
    private int id;

    @Override
    public Map<Integer, User> getUsers() {
        return userMap;
    }

    @Override
    public User getUserById(Integer id) {
        boolean isUserNotFound = true;
        User user = null;

        for (Map.Entry<Integer, User> integerUserEntry : userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == id) {
                isUserNotFound = false;
                user = integerUserEntry.getValue();
                break;
            }
        }

        if (isUserNotFound) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    @Override
    public User addUser(User user) throws ValidationException {
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            throw new ValidationException("Login incorrect");
        }

        // проверка имени - если имя не добавлено, подставляем логин в имя
        if (user.getName().isEmpty() || user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }

        // проверка даты рождения
        LocalDate today = LocalDate.now();
        boolean isBirthDayBeforeToday = user.getBirthday().isBefore(today);

        // если пользователей ещё нет - добавляем первого
        if (userMap.isEmpty() && isBirthDayBeforeToday) {
            id = 1;
            user.setId(id);
            userMap.put(0, user);

            log.info("Добавлен новый пользователь: " + user.getLogin());

            // пользователи уже есть
        } else if (isBirthDayBeforeToday) {
            if (userMap.containsValue(user)) {
                log.info("Попытка добавления пользователя с занятым логином: " + user.getLogin());
                throw new ValidationException("Этот login уже занят");
            }

            id++;
            user.setId(id);
            userMap.put(id - 1, user);

            log.info("Добавлен новый пользователь: " + user.getLogin());

        } else {
            log.info("Ошибка данных при добавлении пользователя");
            throw new ValidationException("Ошибка данных при добавлении пользователя");
        }
        return user;
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        boolean isUserNotFound = true;

        for (int i = 0; i < userMap.size(); i++) {
            if (userMap.get(i).getId() == user.getId()) {
                userMap.replace(i, user);

                log.info("The user data has updated: " + user.getLogin());
                isUserNotFound = false;

                return user;
            }
        }

        if (isUserNotFound) {
            log.info("Trying to update a data of unknown user");
            throw new ValidationException("Trying to update a data of unknown user");
        }
        return user;
    }

    @Override
    public User deleteUser(User user) throws ValidationException {
        int userId = user.getId();

        if (userMap.containsValue(user)) {
            userMap.remove(user);
            log.info("Удаление пользователя из списка: " + user.getLogin());
        } else {
            log.info("Попытка удаления несуществующего пользователя");
            throw new ValidationException("Попытка удаления несуществующего пользователя");
        }
        return user;
    }
}