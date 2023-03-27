package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

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
        filmMap.put(film.getId(), film);
    }

    @PutMapping
    public void updateFilm(@RequestBody Film film) {
        if (filmMap.containsValue(film.getId())) {
            filmMap.put(film.getId(), film);
        }
    }
}
