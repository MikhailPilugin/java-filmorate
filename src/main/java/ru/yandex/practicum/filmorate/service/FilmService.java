package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    public boolean addLike(Integer id, Integer userId);

    public boolean delLike(Integer id, Integer userId);

    public List<Film> getPopularFilms(Integer count);

    List<Film> searchFilms(String query, List<String> by);

}