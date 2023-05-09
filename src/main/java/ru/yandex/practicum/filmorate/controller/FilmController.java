package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@ResponseBody
public class FilmController {
    private FilmDbStorage filmDbStorage;
    private FilmService filmService;

    @Autowired
    public FilmController(FilmDbStorage filmDbStorage, FilmService filmService) {
        this.filmDbStorage = filmDbStorage;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return filmDbStorage.getFilms().values();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Integer id) throws ValidationException {
        return filmDbStorage.getFilmById(id);
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody @Valid Film film) throws ValidationException {
        return filmDbStorage.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody @Valid Film film) throws ValidationException {
        return filmDbStorage.updateFilm(film);
    }

    @DeleteMapping("/films")
    public Film deleteFilm(@RequestBody @Valid Film film) throws ValidationException {
        return filmDbStorage.deleteFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public boolean addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public boolean delRate(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.delLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        return filmService.getPopularFilms(count);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse("error", "Передан некорректный параметр");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInterruptedException(final NullPointerException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ErrorResponse("error", e.getMessage());
    }
}
