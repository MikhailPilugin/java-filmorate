package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashMap;
import java.util.Map;

@Component
public class GenresDbStorage {
    private final Logger log = LoggerFactory.getLogger(GenresDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<Integer, Genres> getGenres() {
        Map<Integer, Genres> genresMap = new HashMap<>();

        // выполняем запрос к базе данных.
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from genre");

        // обрабатываем результат выполнения запроса
        if (genresRows.next()) {
            do {
                Genres genres = new Genres();

                genres.setId(genresRows.getInt("genre_id"));
                genres.setName(genresRows.getString("genre_name"));

                genresMap.put(genres.getId(), genres);
            } while (genresRows.next());
        }
        return genresMap;
    }

    public Genres getGenresById(Integer id) {
        Genres genres = new Genres();

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from genre where genre_id = ?", id);

        // обрабатываем результат выполнения запроса
        if (genresRows.next()) {
            genres.setId(genresRows.getInt("genre_id"));
            genres.setName(genresRows.getString("genre_name"));
        } else {
            throw new IllegalArgumentException("");
        }

        return genres;
    }
}