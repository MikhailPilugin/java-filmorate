package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Service
public class FilmService {
    public Map<Integer, Film> filmMap;

    @Autowired
    public FilmService(Map<Integer, Film> filmMap) {
        this.filmMap = filmMap;
    }

    public Film addLike(long userId, long filmId) {
        Film film;

        for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
            if (integerFilmEntry.getKey() == filmId) {
                film = integerFilmEntry.getValue();
                Set<Long> likes = film.getLikes();
                likes.add(userId);
                film.setLikes(likes);
                filmMap.put(film.getId(), film);
            }
        }
        
        return filmMap.get(filmId);
    }

    public Film delLike(long userId, long filmId) {
        Film film;

        for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
            if (integerFilmEntry.getKey() == filmId) {
                film = integerFilmEntry.getValue();
                Set<Long> likes = film.getLikes();
                likes.remove(userId);
                film.setLikes(likes);
                filmMap.put(film.getId(), film);
            }
        }
        
        return filmMap.get(filmId);
    }

    public TreeMap<Integer, Integer> getPopularFilms() {
        TreeMap<Integer, Integer> likes = new TreeMap<>(new DescOrder());

        for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
            likes.put(integerFilmEntry.getKey(), integerFilmEntry.getValue().getLikes().size());
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(likes.get(i));
        }
        
        return likes;
    }
}

class DescOrder implements Comparator<Integer> {

    @Override
    public int compare(Integer o1, Integer o2) {
        return o2.compareTo(o1);
    }
}