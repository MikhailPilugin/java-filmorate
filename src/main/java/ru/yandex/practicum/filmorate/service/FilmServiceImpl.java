package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.FilmMaker;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class FilmServiceImpl implements FilmService {
    private final Logger log = LoggerFactory.getLogger(FilmServiceImpl.class);
    private final FilmDbStorage filmDbStorage;

    private final JdbcTemplate jdbcTemplate;

    public FilmServiceImpl(FilmDbStorage filmDbStorage, JdbcTemplate jdbcTemplate) {
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
        likeIsAdded = jdbcTemplate.update(sqlQueryTwo,
                likes + 1,
                id) > 0;

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
        likeIsDeleted = jdbcTemplate.update(sqlQueryTwo,
                likes - 1,
                id) > 0;

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

    @Override
    public List<Film> getRecommendations(Integer userId) {
        List<Film> filmsOfSimilarUser = new ArrayList<>();

        String sql = "SELECT DISTINCT USER_ID,  COUNT(FILM_ID) FROM  FILM_LIKES " +
                "    WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_LIKES " +
                "    WHERE USER_ID = ?) AND USER_ID <> ? " +
                "GROUP BY USER_ID " +
                "ORDER BY COUNT(FILM_ID) DESC " +
                "LIMIT 1";

        Integer mostSimilarUser = jdbcTemplate.query(sql,
                        (rs, rowNum) -> rs.getInt("USER_ID"), userId, userId)
                .stream()
                .findAny().orElse(null);

        if (mostSimilarUser != null) {
            sql = "SELECT F.FILM_ID, " +
                    "F.FILM_NAME, " +
                    "F.DESCRIPTION, " +
                    "F.RELEASE_DATE, " +
                    "F.DURATION, " +
                    "M.MPA_ID, " +
                    "M.MPA_NAME, " +
                    "F.LIKES " +
                    "FROM FILM F " +
                    "INNER JOIN MPA_RATING M ON F.MPA_ID=M.MPA_ID " +
                    "INNER JOIN " +
                    "(SELECT * FROM FILM_LIKES " +
                    "WHERE USER_ID=? AND FILM_ID NOT IN " +
                    "(SELECT FILM_ID FROM FILM_LIKES " +
                    "WHERE USER_ID = ?)) " +
                    "FL ON F.FILM_ID = FL.FILM_ID " +
                    "GROUP BY F.FILM_ID ";

            filmsOfSimilarUser = jdbcTemplate.query(sql, new Object[]{mostSimilarUser, userId},
                    new FilmMaker(jdbcTemplate));

        }
        return filmsOfSimilarUser;

    }
}