package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    public Map<Integer, Film> getFilms();

    public Film getFilmById(Integer id) throws ValidationException;

    public Film addFilm(Film film) throws ValidationException;

    public Film updateFilm(Film film) throws ValidationException;

    public Film deleteFilm(Film film) throws ValidationException;
}
