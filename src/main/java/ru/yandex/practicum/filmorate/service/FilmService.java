package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    public Map<Integer, Film> filmMap;

    @Autowired
    public FilmService(Map<Integer, Film> filmMap) {
        this.filmMap = filmMap;
    }

    public Film addLike(long id, long userId) {
        Film film;

        for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
            if (integerFilmEntry.getKey() == id) {
                film = integerFilmEntry.getValue();
                Set<Long> likes = film.getLikes();
                likes.add(userId);
                film.setLikes(likes);
                filmMap.put(film.getId(), film);
            }
        }
        
        return filmMap.get(id);
    }

    public Film delLike(long id, long userId) {
        Film film;

        for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
            if (integerFilmEntry.getKey() == id) {
                film = integerFilmEntry.getValue();
                Set<Long> likes = film.getLikes();
                likes.remove(userId);
                film.setLikes(likes);
                filmMap.put(film.getId(), film);
            }
        }
        
        return filmMap.get(id);
    }

    public List<Integer> getPopularFilms(Long count) {
        List<Integer> allLikes = null;
        List<Integer> sortedLikes = null;

            for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
                allLikes.add(integerFilmEntry.getValue().getLikes().size());
            }

            if (count == null) {
                for (int i = 0; i < 10; i++) {
                    sortedLikes.add(allLikes.get(i));
                }
            } else {
                for (int i = 0; i < count; i++) {
                    sortedLikes.add(allLikes.get(i));
                }
            }

        Collections.reverse(sortedLikes);

            return sortedLikes;
    }
}