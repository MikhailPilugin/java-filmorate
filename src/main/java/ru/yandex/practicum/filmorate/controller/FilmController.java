package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    Map<Integer, Film> filmMap = new HashMap<>();
    int id = 1;

    @GetMapping
    public Collection<?> getFilms() {
        return filmMap.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) throws ValidationException {

        // проверка даты релиза - не раньше 28 декабря 1895 года
        LocalDate releaseDate = film.getReleaseDate();
        LocalDate firstReleaseFilm = LocalDate.of(1895, 12, 28);
        boolean isReleaseDateAfterFirstRelease = releaseDate.isAfter(firstReleaseFilm);

        // если фильмов ещё нет - добавляем первый
        if (filmMap.size() == 0 && isReleaseDateAfterFirstRelease) {
            film.setId(id);
            filmMap.put(id, film);

        // фильмы уже есть
        } else if (isReleaseDateAfterFirstRelease){
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

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) throws ValidationException {

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
}
