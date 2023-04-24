package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {
    InMemoryFilmStorage inMemoryFilmStorage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public boolean addLike(Integer id, Integer userId) {
        Film film = null;
        int indexFilm = 0;
        int statusRadeAdd;
        boolean rateIsAdded = false;

        if (id < 0 || userId < 0) {
            throw new IllegalArgumentException("Отрицательное значение переменной пути");
        }

        for (int i = 1; i <= inMemoryFilmStorage.filmMap.size(); i++) {
            if (inMemoryFilmStorage.filmMap.get(i).getId() == id) {
                film = inMemoryFilmStorage.filmMap.get(i);
                indexFilm = i;
            }
        }

        statusRadeAdd = film.setLikes(userId);
        inMemoryFilmStorage.filmMap.replace(indexFilm, film);

        if (statusRadeAdd == 0) {
            rateIsAdded = true;
        }

        return rateIsAdded;
    }

    public boolean delLike(Integer id, Integer userId) {
        Film film = null;
        int indexFilm = 0;
        int statusRadeDel;
        boolean rateIsDelete = false;

        if (id < 0 || userId < 0) {
            throw new IllegalArgumentException("Отрицательное значение переменной пути");
        }

        for (int i = 1; i <= inMemoryFilmStorage.filmMap.size(); i++) {
            if (inMemoryFilmStorage.filmMap.get(i).getId() == id) {
                film = inMemoryFilmStorage.filmMap.get(i);
                indexFilm = i;
                break;
            }
        }

        statusRadeDel = film.removeLikes(userId);
        inMemoryFilmStorage.filmMap.replace(indexFilm, film);

        if (statusRadeDel == 0) {
            rateIsDelete = true;
        }
        return rateIsDelete;
    }

    public List<Film> getPopularFilms(Integer count) {
        Film film = null;
        List<Film> allRatedFilms = new ArrayList<>();
        List<Film> sortedRatedFilms = new ArrayList<>();

        for (Map.Entry<Integer, Film> integerFilmEntry : inMemoryFilmStorage.filmMap.entrySet()) {
            if (integerFilmEntry.getValue().getRate() != 0) {
                film = integerFilmEntry.getValue();
                allRatedFilms.add(film);
            }
        }

        Collections.reverse(allRatedFilms);

        Collections.sort(allRatedFilms,
                (o2, o1) -> o2.getRate());

        if (count != null) {
            sortedRatedFilms = allRatedFilms.subList(0, count);
        } else {

            int size = allRatedFilms.size();

            if (size < 10) {
                sortedRatedFilms = allRatedFilms.subList(0, size);
            } else {
                sortedRatedFilms = allRatedFilms.subList(0, 11);
            }
        }
        return sortedRatedFilms;
    }
}