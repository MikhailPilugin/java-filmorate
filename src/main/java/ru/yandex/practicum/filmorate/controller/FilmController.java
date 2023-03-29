package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    Map<Integer, Film> filmMap = new HashMap<>();

    @GetMapping
    public Map<Integer, Film> getFilms() {
        return filmMap;
    }

    @PostMapping
    public void addFilm(@RequestBody Film film) {

        LocalDate releaseDate = film.getReleaseDate();
        LocalDate firstFilm = LocalDate.parse("1895-12-28");
        boolean isAfter = releaseDate.isAfter(firstFilm);

        long duration = film.getDuration();

        if (filmMap.size() == 0 && film.getName() != null && film.getDescription().length() <= 200 && isAfter
        && duration > 0) {
            filmMap.put(film.getId(), film);

        } else if (film.getName() != null && film.getDescription().length() <= 200 && isAfter
                && duration > 0){
            for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
                if (integerFilmEntry.getValue().getId() == film.getId()) {
                    System.out.println("Этот id уже занят");
                } else {
                    filmMap.put(film.getId(), film);
                }
            }
        }
    }

    @PutMapping
    public void updateFilm(@RequestBody Film film) {

        for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
            if (integerFilmEntry.getValue().getId() == film.getId()) {
                filmMap.replace(film.getId(), film);
            }
        }
    }
}
