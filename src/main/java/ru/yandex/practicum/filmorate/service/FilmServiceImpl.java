package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.FilmMaker;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmDbStorage filmDbStorage;
    private final DirectorStorage directorStorage;
    private final JdbcTemplate jdbcTemplate;

//    @Override
//    public boolean addLike(Integer id, Integer userId) {
//        String sqlQueryOne = "insert into film_likes (film_id, user_id) values (?, ?)";
//        boolean likeIsAdded;
//        int likes = 0;
//
//        jdbcTemplate.update(sqlQueryOne,
//                id,
//                userId);
//
//        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select likes from film where film_id = ?", id);
//
//        if (likesRows.next()) {
//            likes = likesRows.getInt("likes");
//        }
//
//
//        String sqlQueryTwo = "update film set likes = ? where film_id = ?";
//        likeIsAdded = jdbcTemplate.update(sqlQueryTwo,
//                likes + 1,
//                id) > 0;
//
//        return likeIsAdded;
//    }

    @Override
    public Film addLike(int filmId, int userId) {
        Film filmToLike = filmDbStorage.getFilmById(filmId);
        Set<Integer> likes = filmToLike.getLikes();
        likes.add(userId);
        filmDbStorage.addFilmLikeToRepo(filmToLike, userId);
        return filmToLike;

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
    public List<Film> getPopularFilmsWithDirector(int directorId, String sortBy) {
        directorStorage.getDirectorFromRepoById(directorId);
        return filmDbStorage.getFilmsByDirectorId(directorId, sortBy);
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {
        return filmDbStorage.searchFilms(query.toLowerCase(), by);
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        return filmDbStorage.getCommonFilms(userId, friendId);
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