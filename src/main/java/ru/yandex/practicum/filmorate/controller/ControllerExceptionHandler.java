package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.DubleException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Map;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundException(NotFoundException ex) {
        String erroMessage = ex.getMessage() != null ? ex.getMessage() : "the object does not exists";

        return Map.of("not found exception ", erroMessage);
    }

    @ExceptionHandler({DubleException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> dubleException(DubleException ex) {
        String erroMessage = ex.getMessage() != null ? ex.getMessage() : "the object already exists";
        return Map.of("duble", erroMessage);
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        return new ErrorResponse("RuntimeException", e.getMessage());
    }
}
