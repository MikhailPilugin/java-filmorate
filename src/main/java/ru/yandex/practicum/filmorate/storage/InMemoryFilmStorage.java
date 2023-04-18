package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    public FilmService filmService;
    private int id = 1;

    @Autowired
    public InMemoryFilmStorage(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return filmService.filmMap;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        LocalDate releaseDate = film.getReleaseDate();
        LocalDate firstReleaseFilm = LocalDate.of(1895, 12, 28);
        boolean isReleaseDateAfterFirstRelease = releaseDate.isAfter(firstReleaseFilm);

        if (filmService.filmMap.size() == 0 && isReleaseDateAfterFirstRelease) {
            film.setId(id);
            filmService.filmMap.put(id, film);

        } else if (isReleaseDateAfterFirstRelease) {
            for (Map.Entry<Integer, Film> integerFilmEntry : filmService.filmMap.entrySet()) {
                if (integerFilmEntry.getValue().getName() == film.getName()) {
                    System.out.println("Этот название уже занято");

                    log.info("Попытка добавить занятое название фильма: " + film.getName());
                } else {
                    film.setId(++id);

                    filmService.filmMap.put(film.getId(), film);

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

        for (Map.Entry<Integer, Film> integerFilmEntry : filmService.filmMap.entrySet()) {
            if (integerFilmEntry.getValue().getId() == filmId) {
                filmService.filmMap.replace(film.getId(), film);

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

        for (Map.Entry<Integer, Film> integerFilmEntry : filmService.filmMap.entrySet()) {
            if (integerFilmEntry.getValue().getId() == filmId) {
                filmService.filmMap.remove(film.getId(), film);

                log.info("Удаление фильма из списка: " + film.getName());
            } else {
                log.info("Попытка удаления несуществующего фильма");
                throw new ValidationException("Попытка удаления несуществующего фильма");
            }
        }
        return film;
    }
}
