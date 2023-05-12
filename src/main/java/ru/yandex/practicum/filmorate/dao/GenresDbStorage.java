package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.HashMap;
import java.util.Map;

@Repository
public class GenresDbStorage {
    private final Logger log = LoggerFactory.getLogger(GenresDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<Integer, Genres> getAll() {
        Map<Integer, Genres> genresMap = new HashMap<>();

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from genre");

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

    public Genres getById(Integer id) throws IllegalArgumentException {
        Genres genres = new Genres();

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from genre where genre_id = ?", id);

        if (genresRows.next()) {
            genres.setId(genresRows.getInt("genre_id"));
            genres.setName(genresRows.getString("genre_name"));
        } else {
            throw new IllegalArgumentException("genre id not found");
        }

        return genres;
    }
}