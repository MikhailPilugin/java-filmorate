package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    @GetMapping
    public Map<Integer, Film> getFilms() {
        return inMemoryFilmStorage.getFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) throws ValidationException {
        return inMemoryFilmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) throws ValidationException {
        return inMemoryFilmStorage.updateFilm(film);
    }

    @DeleteMapping
    public Film deleteFilm(@RequestBody @Valid Film film) throws ValidationException {
        return inMemoryFilmStorage.deleteFilm(film);
    }

    @PutMapping
    public Film addLike(long userId, long filmId) {
        return inMemoryFilmStorage.filmService.addLike(userId, filmId);
    }

    @DeleteMapping
    public Film delLike(long userId, long filmId) {
        return inMemoryFilmStorage.filmService.delLike(userId, filmId);
    }

    @GetMapping
    public TreeMap<Integer, Integer> getPopularFilms() {
        return inMemoryFilmStorage.filmService.getPopularFilms();
    }
}
