package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director getDirectorFromRepoById(int directorId);

    List<Director> getAllDirectors();

    Director addDirector(Director director);

    Director updateDirector(Director director);

    boolean deleteDirectorById(int directorId);
}
