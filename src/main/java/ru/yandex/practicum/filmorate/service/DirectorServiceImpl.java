package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    @Override
    public Director getDirectorById(int directorId) {
        log.debug("Director whit id = \"{}\" ", directorId);
        return directorStorage.getDirectorFromRepoById(directorId);
    }

    @Override
    public List<Director> getAllDirectors() {
        log.debug("There are {} directors in filmorate", directorStorage.getAllDirectors().size());
        return directorStorage.getAllDirectors();
    }

    @Override
    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    @Override
    public boolean deleteDirectorById(int directorId) {
        log.debug("Director whit id = \"{}\" deleted", directorId);
        return directorStorage.deleteDirectorById(directorId);
    }
}
