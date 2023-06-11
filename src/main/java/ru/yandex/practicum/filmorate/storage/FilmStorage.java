package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> getFilms();

    Film getFilmById(Integer id) throws ValidationException;

    Film addFilm(Film film) throws ValidationException;

    Film updateFilm(Film film) throws ValidationException;

    Film deleteFilm(Film film) throws ValidationException;

    boolean deleteFilmById(Integer id) throws ValidationException;

    List<Film> getFilmsByDirectorId(int directorId, String sortBy);

    List<Film> searchFilms(String query, List<String> by);

    List<Film> getFilmsSortedByPopularity();

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    Film addFilmLikeToRepo(Film filmToLike, int userId);
}