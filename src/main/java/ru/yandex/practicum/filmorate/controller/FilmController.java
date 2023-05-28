package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@ResponseBody
@Slf4j
public class FilmController {
    private FilmDbStorage filmDbStorage;
    private FilmServiceImpl filmServiceImpl;

    @Autowired
    public FilmController(FilmDbStorage filmDbStorage, FilmServiceImpl filmServiceImpl) {
        this.filmDbStorage = filmDbStorage;
        this.filmServiceImpl = filmServiceImpl;
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
        return filmServiceImpl.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public boolean delRate(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmServiceImpl.delLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        return filmServiceImpl.getPopularFilms(count);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getFilmsWithDirector(@PathVariable int directorId, @RequestParam(defaultValue = "year") String sortBy) {
        log.debug("Director whit id = \"{}\" ", directorId);
        return filmServiceImpl.getPopularFilmsWithDirector(directorId, sortBy);
    }

    @GetMapping("/films/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam List<String> by) {
        return filmServiceImpl.searchFilms(query, by);
    }


    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Integer id) {
        return filmServiceImpl.getRecommendations(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse("error", "Передан некорректный параметр");
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
