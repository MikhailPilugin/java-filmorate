package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Map;

public class FilmDbStorage implements FilmStorage {
    @Override
    public Map<Integer, Film> getFilms() {
        return null;
    }

    @Override
    public Film getFilmById(Integer id) {
        return null;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        return null;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        return null;
    }

    @Override
    public Film deleteFilm(Film film) throws ValidationException {
        return null;
    }
}
