package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends Exception{
    public ValidationException(String message) {
        super("Ошибка ввода данных: " + message);
    }
}
