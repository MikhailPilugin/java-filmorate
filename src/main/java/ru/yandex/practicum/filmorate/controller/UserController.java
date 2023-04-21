package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@ResponseBody
public class UserController {
    private InMemoryUserStorage inMemoryUserStorage;
    private UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return InMemoryUserStorage.userList;
    }

//    @GetMapping("/users/{id}")
//    public User getUser(@PathVariable long id) throws ValidationException {
//        User user = null;
//        boolean userNotFound = true;
//
//        for (Map.Entry<Integer, User> integerUserEntry : inMemoryUserStorage.userMap.entrySet()) {
//            if (integerUserEntry.getKey() == id) {
//                user = integerUserEntry.getValue();
//                userNotFound = false;
//            }
//        }
//
//        if (userNotFound) {
//            throw new IllegalArgumentException("Пользователь не найден");
//        }
//        return user;
//    }

    @PostMapping("/users")
    public void addUser(@RequestBody @Valid User user) throws ValidationException {
        System.out.println("Map before adding user: " + InMemoryUserStorage.userList);

        inMemoryUserStorage.addUser(user);

        System.out.println("Map after adding user: " + InMemoryUserStorage.userList);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody @Valid User user) throws ValidationException {
        return inMemoryUserStorage.updateUser(user);
    }

    @DeleteMapping("/users")
    public User deleteUser(@RequestBody @Valid User user) throws ValidationException {
        return inMemoryUserStorage.deleteUser(user);
    }



    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) throws ValidationException {
        System.out.println("Map before adding friend: " + InMemoryUserStorage.userList);

        System.out.println("id: " + id + "  firendId: " + friendId);

//        if (id < 0 || friendId < 0) {
//            throw new IllegalArgumentException("Отрицательное значение переменной пути");
//        }

        //////////////////////////////////////////////////

//        System.out.println("test");
//
//        User user1 = new User();
//        User user2 = new User();
//
//        System.out.println("test 2");
//
//        user1.setId(3);
//        user1.setName("qq");
//        user1.setLogin("qq");
//        user1.setEmail("mail1@mail.ru");
//        user1.setBirthday(LocalDate.parse("1946-08-20"));
//        user1.setFriends(null);
//
//        System.out.println("test 3");
//
//        user1.setId(4);
//        user1.setName("ww");
//        user1.setLogin("ww");
//        user1.setEmail("mail@mail.ru");
//        user1.setBirthday(LocalDate.parse("1946-08-21"));
//        user1.setFriends(null);
//
//        System.out.println("test 4");

//        InMemoryUserStorage.userMap.put(3, user1);

//        inMemoryUserStorage.addUser(user1);

//        System.out.println("userMap afterAdd friend 1: " + InMemoryUserStorage.userList);


//        inMemoryUserStorage.addUser(user2);

//        InMemoryUserStorage.userMap.put(4, user2);

        userService.addFriend(id, friendId);

        System.out.println("userMap afterAdd friend 1: " + InMemoryUserStorage.userList);

//        System.out.println("userMap afterAdd friend 1: " + InMemoryUserStorage.userList);
//        System.out.println("userMap afterAdd friend 1: " + InMemoryUserStorage.userList);
//
//        System.out.println("userMap: " + InMemoryUserStorage.userList);


//        User user = null;
//        List<Integer> userList = null;

//        for (Map.Entry<Integer, User> integerUserEntry : InMemoryUserStorage.userMap.entrySet()) {
//            if (integerUserEntry.getKey() == id) {
//                user = integerUserEntry.getValue();
//            }
//        }

//        System.out.println("user 1: " + user);
//        System.out.println("friends: " + user.getFriends());
//        userList = user.getFriends();
//
//        System.out.println("userList: " + userList);


        /////////////////////////////////////////////////

//        for (Map.Entry<Integer, User> integerUserEntry : InMemoryUserStorage.userMap.entrySet()) {
//            if (integerUserEntry.getKey() == id) {
//                System.out.println("user 1" + integerUserEntry.getValue());
//
//                User user = integerUserEntry.getValue();
//                System.out.println("user 2" + user);
//
//                Set<Long> userSet = new HashSet<>();
//                userSet = user.getFriends();
//
//                System.out.println("userSet 1 " + userSet);
//
//                Long num = Long.valueOf(friendId);
//
//                System.out.println("num long: " + num);
//
//                userSet.add(num);
//
//                System.out.println("userSet 2 " + userSet);
//
//                user.setFriends(Long.valueOf(friendId));
//
//                System.out.println("user 3" + user);
//            }
//        }


//        inMemoryUserStorage.userService.addFriend(id, friendId);
//
//        userService.addFriend(id, friendId);
//
//        System.out.println("Map after adding friend: " + InMemoryUserStorage.userMap.values());

    }

//    @DeleteMapping("/users/{id}/friends/{friendId}")
//    public User delFriend(@PathVariable Long id, @PathVariable Long friendId) {
//        return inMemoryUserStorage.userService.delFriend(id, friendId);
//    }

    @GetMapping("/users/{id}/friends")
    public List<Integer> getFriends(@PathVariable int id) {
        System.out.println(InMemoryUserStorage.userList.get((int) id).getFriends());
        System.out.println(inMemoryUserStorage.userService.getFriend(id));
        System.out.println(InMemoryUserStorage.userList);


        return InMemoryUserStorage.userList.get(id).getFriends();
    }

//    @GetMapping("/users/{id}/friends/common/{otherId}")
//    public List<Long> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
//        return inMemoryUserStorage.userService.getCommonFriends(id, otherId);
//    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse("error", "Передан некорректный параметр");
    }

    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInterruptedException(final NullPointerException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ErrorResponse("error", e.getMessage());
    }
}
