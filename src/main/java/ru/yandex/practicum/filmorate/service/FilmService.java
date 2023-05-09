package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService implements FilmServiceInterface {
    private final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmDbStorage filmDbStorage;

    private final JdbcTemplate jdbcTemplate;

    public FilmService(FilmDbStorage filmDbStorage, JdbcTemplate jdbcTemplate) {
        this.filmDbStorage = filmDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addLike(Integer id, Integer userId) {
        String sqlQueryOne = "insert into film_likes (film_id, user_id) values (?, ?)";
        boolean likeIsAdded;
        int likes = 0;

        jdbcTemplate.update(sqlQueryOne,
                id,
                userId);

        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select likes from film where film_id = ?", id);

        if (likesRows.next()) {
            likes = likesRows.getInt("likes");
        }


        String sqlQueryTwo = "update film set likes = ? where film_id = ?";
        likeIsAdded = jdbcTemplate.update(sqlQueryTwo
                , likes + 1
                , id) > 0;

        return likeIsAdded;
    }

    @Override
    public boolean delLike(Integer id, Integer userId) {
        String sqlQuery = "delete from film_likes where film_id = ? and user_id = ?";
        int likes = 0;
        boolean likeIsDeleted;

        if (id < 0 || userId < 0) {
            throw new IllegalArgumentException("argument is must be above zero");
        }

        jdbcTemplate.update(sqlQuery, id, userId);

        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select likes from film where film_id = ?", id);

        if (likesRows.next()) {
            likes = likesRows.getInt("likes");
        }

        String sqlQueryTwo = "update film set likes = ? where film_id = ?";
        likeIsDeleted = jdbcTemplate.update(sqlQueryTwo
                , likes - 1
                , id) > 0;

        return likeIsDeleted;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa_rating");

        Film film = new Film();
        List<Film> allRatedFilms = new ArrayList<>();
        List<Film> sortedRatedFilms = new ArrayList<>();

        if (count == null) {
            SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film order by likes desc limit 10");

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

                    allRatedFilms.add(film);

                } while (filmRows.next());
            }

        } else {
            SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film order by likes limit " + count);

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

                    allRatedFilms.add(film);

                } while (filmRows.next());
            }
        }
        return allRatedFilms;
    }
}