package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    public UserService userService;
    private int id = 1;

    @Autowired
    public InMemoryUserStorage(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Map<Integer, User> getUsers() {
        return userService.userMap;
    }

    @Override
    public User addUser(User user) throws ValidationException {
        // проверка логина
        String login = user.getLogin();
        String space = " ";
        boolean isLoginContainsSpace = login.contains(space);
        boolean isLoginNotEmpty = !login.isEmpty();

        // проверка имени - если имя не добавлено, подставляем логин в имя
        if (user.getName().isEmpty() || user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }

        // проверка даты рождения
        LocalDate today = LocalDate.now();
        boolean isBirthDayBeforeToday = user.getBirthday().isBefore(today);

        // если пользователей ещё нет - добавляем первого
        if (userService.userMap.size() == 0 && !isLoginContainsSpace && isLoginNotEmpty && isBirthDayBeforeToday) {
            user.setId(id);
            userService.userMap.put(id, user);

            log.info("Добавлен новый пользователь: " + user.getLogin());

            // пользователи уже есть
        } else if (isLoginNotEmpty && isBirthDayBeforeToday && !isLoginContainsSpace) {

            for (Map.Entry<Integer, User> integerUserEntry : userService.userMap.entrySet()) {
                String userLogin = integerUserEntry.getValue().getLogin();

                if (user.getLogin().equals(userLogin)) {
                    log.info("Попытка добавления пользователя с занятым логином: " + user.getLogin());
                    throw new ValidationException("Этот login уже занят");
                }
            }

            user.setId(++id);

            userService.userMap.put(user.getId(), user);
            log.info("Добавлен новый пользователь: " + user.getLogin());

        } else {
            log.info("Ошибка данных при добавлении пользователя");
            throw new ValidationException("Ошибка данных при добавлении пользователя");
        }
        return user;
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        int userId = user.getId();

        if (userService.userMap.containsKey(userId)) {
            for (Map.Entry<Integer, User> integerUserEntry : userService.userMap.entrySet()) {
                if (integerUserEntry.getValue().getId() == userId) {
                    userService.userMap.put(userId, user);
                    log.info("Данные пользователя обновлены: " + user.getLogin());
                }
            }
        } else {
            log.info("Попытка обновления данных несуществующего пользователя");
            throw new ValidationException("Попытка обновления данных несуществующего пользователя");
        }
        return user;
    }

    @Override
    public User deleteUser(User user) throws ValidationException {
        int userId = user.getId();

        for (Map.Entry<Integer, User> integerUserEntry : userService.userMap.entrySet()) {
            if (integerUserEntry.getValue().getId() == userId) {
                userService.userMap.remove(userId, user);
                log.info("Удаление пользователя из списка: " + user.getLogin());
            } else {
                log.info("Попытка удаления несуществующего пользователя");
                throw new ValidationException("Попытка удаления несуществующего пользователя");
            }
        }
        return user;
    }
}