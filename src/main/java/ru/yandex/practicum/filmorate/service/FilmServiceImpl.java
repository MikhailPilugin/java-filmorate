package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.FilmMaker;
import ru.yandex.practicum.filmorate.dao.GenresDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.UserFeedStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmDbStorage filmDbStorage;
    private final GenresDbStorage genresDbStorage;
    private final DirectorStorage directorStorage;
    private final JdbcTemplate jdbcTemplate;
    private final UserFeedStorage userFeedStorage;

    @Override
    public Film addLike(int filmId, int userId) {
        Film filmToLike = filmDbStorage.getFilmById(filmId);
        Set<Integer> likes = filmToLike.getLikes();
        likes.add(userId);
        filmDbStorage.addFilmLikeToRepo(filmToLike, userId);

        userFeedStorage.likeEvent(userId, filmId, "ADD");

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

        userFeedStorage.likeEvent(userId, id, "REMOVE");

        return likeIsDeleted;
    }

    @Override
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        List<Film> selectedPopularFilm = new ArrayList<>();

        if (count <= 0) {
            log.error("Запрошено не корректное количество фильмов {}", count);
            throw new IllegalArgumentException("argument is must be above zero");
        }

        if (year != null && (year >= 1895 && year >= LocalDate.now().getYear())) {
            log.error("Year parameter value is not correct {}", year);
            throw new IllegalArgumentException("Year parameter value must be in 1895 - " +
                    LocalDate.now().getYear() + " period");
        }

        if (genreId != null && (!genresDbStorage.getAll().containsKey(genreId))) {
            log.error("genre ID is not correct {}", genreId);
            throw new IllegalArgumentException("Genre ID parameter value must be in the range from 1 " +
                    " to " + genresDbStorage.getAll().size());
        }

        for (Film film : filmDbStorage.getFilmsSortedByPopularity()) {

            if (selectedPopularFilm.size() < count) {
                if (genreId != null) {
                    for (Genres genre : film.getGenres()) {
                        if (genre.getId() == genreId) {
                            if (year != null) {
                                if (film.getReleaseDate().getYear() == year) {
                                    selectedPopularFilm.add(film);
                                }
                            } else {
                                selectedPopularFilm.add(film);
                            }
                        }
                    }
                } else {
                    if (year != null) {
                        if (film.getReleaseDate().getYear() == year) {
                            selectedPopularFilm.add(film);
                        }
                    } else {
                        selectedPopularFilm.add(film);
                    }
                }
            }
        }
        return selectedPopularFilm;
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