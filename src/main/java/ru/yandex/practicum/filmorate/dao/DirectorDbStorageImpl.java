package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DubleException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director getDirectorFromRepoById(int directorId) {
        String sqlQuery = "SELECT director_id, director_name FROM DIRECTORS WHERE director_id = " + directorId;
        Director result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    sqlQuery,
                    (resultSet, rowNum) -> {
                        Director director = new Director();
                        director.setId(Integer.parseInt(resultSet.getString("director_id")));
                        director.setName(resultSet.getString("director_name"));
                        return director;
                    });
        } catch (EmptyResultDataAccessException e) {
            // обработка ситуации, когда пользователя с указанным ID не было найдено
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Director not found");
        }
        return result;
    }

    @Override
    public List<Director> getAllDirectors() {
        String sqlQuery = "SELECT DIRECTOR_ID, DIRECTOR_NAME FROM DIRECTORS";
        List<Director> directorList = jdbcTemplate.query(
                sqlQuery, (resultSet, rowNum) -> {
                    Director director = new Director();
                    director.setId(Integer.parseInt(resultSet.getString("director_id")));
                    director.setName(resultSet.getString("director_name"));
                    return director;

                });


        return directorList;
    }

    @SneakyThrows
    @Override
    public Director addDirector(Director director) {
        String checkQuery = "SELECT COUNT(*) FROM DIRECTORS WHERE DIRECTOR_NAME = ?";
        int count = jdbcTemplate.queryForObject(checkQuery, new Object[]{director.getName()}, Integer.class);
        if (count > 0) {
            throw new DubleException("Director '" + director.getName() + "' already exists");
        }
        String sqlQuery = "INSERT INTO DIRECTORS (DIRECTOR_NAME) SELECT (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId((Integer) keyHolder.getKey());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String checkQuery = "SELECT COUNT(*) FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        int count = jdbcTemplate.queryForObject(checkQuery, new Object[]{director.getId()}, Integer.class);
        if (count == 0) {
            throw new NotFoundException(HttpStatus.BAD_REQUEST, "Director with name = '" + director.getName() + "' and with id = '" + director.getId() + "' does not exists");
        }

        String sqlQuery = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery,
                director.getName(),
                director.getId());
        return director;
    }

    @Override
    public boolean deleteDirectorById(int directorId) {
        String queryDelere = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        boolean deleteDirector = jdbcTemplate.update(queryDelere, directorId) > 0;
        if (!deleteDirector) {
            throw new NotFoundException(HttpStatus.NOT_MODIFIED, "Director does not delete");
        }
        return true;

    }
}
