package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film addLike(int filmId, int userId);

    boolean delLike(Integer id, Integer userId);

    List<Film> getPopularFilms(Integer count);

    List<Film> getPopularFilmsWithDirector(int directorId, String sortBy);

    List<Film> searchFilms(String query, List<String> by);

    List<Film> getCommonFilms(Integer userId, Integer friendId);
 
    List<Film> getRecommendations(Integer userId);
 
}