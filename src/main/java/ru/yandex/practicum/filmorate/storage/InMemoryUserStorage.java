package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    public static List<User> userList = new ArrayList<>();

    public UserService userService;
    private int id;

    @Autowired
    public InMemoryUserStorage(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<User> getUsers() {
        return userList;
    }

    @Override
    public User addUser(User user) throws ValidationException {
        if(user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank()) {
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
        if (userList.isEmpty() && isBirthDayBeforeToday) {
            id = 1;
            user.setId(id);
            userList.add(user);

            log.info("Добавлен новый пользователь: " + user.getLogin());

            // пользователи уже есть
        } else if (isBirthDayBeforeToday) {
            if (userList.contains(user)) {
                    log.info("Попытка добавления пользователя с занятым логином: " + user.getLogin());
                    throw new ValidationException("Этот login уже занят");
                }

            id++;
            user.setId(id);
            userList.add(user);
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
        int index = 0;

        if (userList.contains(user)) {
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getId() == user.getId()) {
                    index = i;
                }
            }

            userList.set(index, user);
            log.info("Данные пользователя обновлены: " + user.getLogin());
        } else {
            log.info("Попытка обновления данных несуществующего пользователя");
            throw new ValidationException("Попытка обновления данных несуществующего пользователя");
        }
        return user;
    }

    @Override
    public User deleteUser(User user) throws ValidationException {
        int userId = user.getId();

        if (userList.contains(user)) {
                userList.remove(user);
                log.info("Удаление пользователя из списка: " + user.getLogin());
            } else {
                log.info("Попытка удаления несуществующего пользователя");
                throw new ValidationException("Попытка удаления несуществующего пользователя");
            }
        return user;
    }
}