package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    public static Map<Integer, Film> filmMap = new HashMap<>();
    public FilmService filmService;
    private int id;

    @Autowired
    public InMemoryFilmStorage(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return filmMap;
    }

    @Override
    public Film getFilmById(Integer id) {
        Film film = null;
        boolean isFilmNotFound = true;

        for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
            if (integerFilmEntry.getValue().getId() == id) {
                isFilmNotFound = false;
                film = integerFilmEntry.getValue();
                break;
            }
        }

        if (isFilmNotFound) {
            throw new IllegalArgumentException("User not found");
        }
        return film;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        LocalDate releaseDate = film.getReleaseDate();
        LocalDate firstReleaseFilm = LocalDate.of(1895, 12, 28);
        boolean isReleaseDateAfterFirstRelease = releaseDate.isAfter(firstReleaseFilm);

        if (filmMap.size() == 0 && isReleaseDateAfterFirstRelease) {
            id = 1;
            film.setId(id);
            filmMap.put(id, film);

        } else if (isReleaseDateAfterFirstRelease) {
            for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
                if (integerFilmEntry.getValue().getName() == film.getName()) {
                    System.out.println("Этот название уже занято");

                    log.info("Попытка добавить занятое название фильма: " + film.getName());
                } else {
                    film.setId(++id);

                    filmMap.put(film.getId(), film);

                    log.info("Добавлен новый фильм: " + film.getName());
                }
            }
        } else {
            log.info("Ошибка данных при добавлении фильма");

            throw new ValidationException("Ошибка данных при добавлении фильма");
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        int filmId = film.getId();

        for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
            if (integerFilmEntry.getValue().getId() == filmId) {
                filmMap.replace(film.getId(), film);

                log.info("Данные фильма обновлены: " + film.getName());
            } else {
                log.info("Попытка обновления данных несуществующего фильма");
                throw new ValidationException("Попытка обновления данных несуществующего фильма");
            }
        }
        return film;
    }

    @Override
    public Film deleteFilm(Film film) throws ValidationException {
        int filmId = film.getId();

        for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
            if (integerFilmEntry.getValue().getId() == filmId) {
                filmMap.remove(film.getId(), film);

                log.info("Удаление фильма из списка: " + film.getName());
            } else {
                log.info("Попытка удаления несуществующего фильма");
                throw new ValidationException("Попытка удаления несуществующего фильма");
            }
        }
        return film;
    }
}
