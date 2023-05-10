package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.GenresDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@ResponseBody
public class GenresController {

    private GenresDbStorage genresDbStorage;

    @Autowired
    public GenresController(GenresDbStorage genresDbStorage) {
        this.genresDbStorage = genresDbStorage;
    }

    @GetMapping("/genres")
    public Collection<Genres> getGenres() {
        return genresDbStorage.getGenres().values();
    }

    @GetMapping("/genres/{id}")
    public Genres getUser(@PathVariable Integer id) {
        return genresDbStorage.getGenresById(id);
    }

//    @PutMapping("/genres")
//    public Genres updateGenre(@RequestBody @Valid Genres genres) throws ValidationException {
//        return genresDbStorage.updateUser(user);
//    }




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
