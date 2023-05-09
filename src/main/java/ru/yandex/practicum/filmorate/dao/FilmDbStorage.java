package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa_rating");
        Map<Integer, Film> filmMap = new HashMap<>();

        // выполняем запрос к базе данных.
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film");

        // обрабатываем результат выполнения запроса
        if (filmRows.next()) {
            do {
                int mpaId = 0;
                String mpaName = null;

                do {
                    if (mpaRows.next()) {
                        mpaId = mpaRows.getInt("mpa_id");
                        mpaName = mpaRows.getString("mpa_name");
                    }
                } while (mpaId != filmRows.getInt("mpa_id"));

                Film film = new Film();
                Mpa mpa = new Mpa();
                mpa.setId(mpaId);
                mpa.setName(mpaName);

                film.setId(Integer.valueOf(filmRows.getString("film_id")));
                film.setName(filmRows.getString("film_name"));
                film.setDescription(filmRows.getString("description"));
                film.setReleaseDate(LocalDate.parse(filmRows.getString("release_date")));
                film.setDuration(filmRows.getLong("duration"));
                film.setMpa(mpa);
                film.setLikes(filmRows.getInt("likes"));

                filmMap.put(film.getId(), film);
            } while (filmRows.next());
        }

        return filmMap;
    }

    @Override
    public Film getFilmById(Integer id) throws ValidationException {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa_rating");
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where film_id = ?", id);
        Film film = new Film();

        if (filmRows.next()) {
            int mpaId = 0;
            String mpaName = null;

            do {
                if (mpaRows.next()) {
                    mpaId = mpaRows.getInt("mpa_id");
                    mpaName = mpaRows.getString("mpa_name");
                }
            } while (mpaId != filmRows.getInt("mpa_id"));

            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            mpa.setName(mpaName);

            film.setId(filmRows.getInt("film_id"));
            film.setName(filmRows.getString("film_name"));
            film.setDescription(filmRows.getString("description"));
            film.setReleaseDate(LocalDate.parse(filmRows.getString("release_date")));
            film.setDuration(filmRows.getInt("duration"));
            film.setMpa(mpa);
            film.setRate(filmRows.getInt("likes"));
        } else {
            throw new IllegalArgumentException("film id not found");
        }

        return film;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        List<Genres> genres = film.getGenres();

        if (genres.size() > 0) {
            film.setGenres(genres);
        }

        LocalDate releaseDate = film.getReleaseDate();
        LocalDate firstReleaseFilm = LocalDate.of(1895, 12, 28);

        if (!releaseDate.isAfter(firstReleaseFilm)) {
            throw new ValidationException("the release date of this film is wrong");
        }

        String sqlQuery = "insert into film (film_name, description, release_date, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, String.valueOf(film.getReleaseDate()));
            stmt.setString(4, String.valueOf(film.getDuration()));
            stmt.setString(5, String.valueOf(film.getMpa()));
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());

        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        SqlRowSet getLastIdRows = jdbcTemplate.queryForRowSet("select * from film order by film_id desc limit 1");

        List<Genres> genres = film.getGenres();

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where film_id = ?", film.getId());

        if (!filmRows.next()) {
            throw new IllegalArgumentException("film id not found");
        }

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select mpa_name from mpa_rating where mpa_id = ?",
                film.getMpa().getId());

        Mpa mpa = new Mpa();
        mpa.setId(film.getMpa().getId());
        film.setMpa(mpa);
        film.setGenres(genres);

        if (mpaRows.next()) {
            mpa.setName(mpaRows.getString("mpa_name"));
        }

        String sqlQuery = "update film set " +
                "film_name = ?, release_date = ?, description = ?, duration = ?, likes = ?, mpa_id = ?" +
                " where film_id = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getReleaseDate()
                , film.getDescription()
                , film.getDuration()
                , film.getRate()
                , film.getMpa().getId()
                , film.getId());

        return film;
    }

    @Override
    public Film deleteFilm(Film film) throws ValidationException {
        String sqlQuery = "delete from film where id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        return film;
    }
}
