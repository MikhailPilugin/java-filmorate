package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    boolean addLike(Integer id, Integer userId);

    boolean delLike(Integer id, Integer userId);

    List<Film> getPopularFilms(Integer count);

    List<Film> getPopularFilmsWithDirector(int directorId, String sortBy);
 }