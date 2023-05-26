package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping("/{directorId}")
    public Director getDirectorById(@PathVariable int directorId) {
        var directorById = directorService.getDirectorById(directorId);
        log.debug("Director whit id = \"{}\" ", directorId);
        return directorById;
    }

    @DeleteMapping("/{directorId}")
    public Director deliteDirectorById(@PathVariable int directorId) {
        Director director = directorService.getDirectorById(directorId);
        Boolean directorById = directorService.deleteDirectorById(directorId);
        if (directorById) {
            log.debug("Director whit id = \"{}\" deleted", directorId);
        } else throw new NotFoundException(HttpStatus.NOT_MODIFIED, "Director does not delete");
        return director;
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        var directors = directorService.getAllDirectors();
        log.debug("There are {} directors in filmorate", directorService.getAllDirectors().size());
        return directors;
    }

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        var directorAdded = directorService.addDirector(director);

        return directorAdded;
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        var directorUpdated = directorService.updateDirector(director);
        return directorUpdated;
    }
}
