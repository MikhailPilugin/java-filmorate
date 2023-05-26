package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends Throwable {
    public NotFoundException(HttpStatus badRequest, String wrong_parameters) {
    }
}
