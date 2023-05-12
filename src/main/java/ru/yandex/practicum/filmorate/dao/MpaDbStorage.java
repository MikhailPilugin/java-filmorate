package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MpaDbStorage {
    private final Logger log = LoggerFactory.getLogger(MpaDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<Integer, Mpa> getMpa() {
        Map<Integer, Mpa> mpaMap = new HashMap<>();

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa_rating");

        if (mpaRows.next()) {
            do {
                Mpa mpa = new Mpa();

                mpa.setId(mpaRows.getInt("mpa_id"));
                mpa.setName(mpaRows.getString("mpa_name"));

                mpaMap.put(mpa.getId(), mpa);
            } while (mpaRows.next());
        }

        return mpaMap;
    }

    public Mpa getMpaById(Integer id) throws IllegalArgumentException {
        Mpa mpa = new Mpa();

        SqlRowSet mpaRowsNotFound = jdbcTemplate.queryForRowSet("select * from mpa_rating where mpa_id = ?", id);

        if (!mpaRowsNotFound.next()) {
            throw new IllegalArgumentException("mpa not found");
        }

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa_rating where mpa_id = ?", id);

        if (mpaRows.next()) {
            mpa.setId(mpaRows.getInt("mpa_id"));
            mpa.setName(mpaRows.getString("mpa_name"));
        }
        return mpa;
    }
}
