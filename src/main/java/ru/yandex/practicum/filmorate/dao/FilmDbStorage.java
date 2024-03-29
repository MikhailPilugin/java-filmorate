package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilmLikeToRepo(Film filmToLike, int userId) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID)" +
                "SELECT ?, ?" +
                "WHERE NOT EXISTS (" +
                "    SELECT 1" +
                "    FROM FILM_LIKES" +
                "    WHERE FILM_ID = ? AND USER_ID = ?" +
                ")";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, filmToLike.getId());
            stmt.setInt(2, userId);
            stmt.setInt(3, filmToLike.getId());
            stmt.setInt(4, userId);
            return stmt;
        }, keyHolder);


        return filmToLike;
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        List<Object> objectList = new ArrayList<>();
        objectList.add(userId);
        objectList.add(friendId);
        String sqlQuery = "SELECT FILM.film_id, film_name, FILM.description, release_date, duration, FILM.mpa_id, MPA_RATING.mpa_name, likes " +
                "                FROM FILM " +
                "JOIN MPA_RATING ON MPA_RATING.MPA_ID = FILM.mpa_id " +
                "              WHERE film_id IN ( " +
                "SELECT film_id " +
                "FROM film_likes " +
                " WHERE film_id  IN  (SELECT film_id " +
                "              FROM film_likes " +
                "             WHERE user_id = ?) " +
                "AND film_id  IN (SELECT film_id " +
                "           FROM film_likes " +
                "      WHERE user_id = ? ) " +
                ") " +
                "         ORDER BY likes DESC ";


        List<Film> allFilms = this.jdbcTemplate.query(
                sqlQuery,
                objectList.toArray(),
                (resultSet, rowNum) -> {
                    Film film = new Film();
                    film.setId(Integer.parseInt(resultSet.getString("film_id")));
                    film.setName(resultSet.getString("film_name"));
                    film.setDescription(resultSet.getString("description"));
                    film.setDuration(Integer.parseInt(resultSet.getString("duration")));
                    film.setReleaseDate(LocalDate.parse(resultSet.getString("release_date")));

                    String likes = resultSet.getString("likes");
                    if (likes != null) {
                        film.setLikes(Integer.parseInt(resultSet.getString("likes")));
                    }
                    int mpaId = Integer.parseInt(resultSet.getString("mpa_id"));

                    String mpaName = resultSet.getString("mpa_name");
                    Mpa mpa = new Mpa(mpaId, mpaName);
                    film.setMpa(mpa);
                    return film;
                });
        return allFilms;
    }


    @Override
    public List<Film> searchFilms(String query, List<String> by) {

        String sqlQuery = "";
        List<Object> params = new ArrayList<>();

        if (by.size() > 2) {
            throw new NotFoundException(HttpStatus.BAD_REQUEST, "wrong parameters");
        } else if (by.size() == 2 && by.contains("title") && by.contains("director")) {
            sqlQuery = "SELECT FILM.film_id, film_name, FILM.description, release_date, duration, FILM.mpa_id, MPA_RATING.mpa_name, likes, D.director_name " +
                    "FROM FILM " +
                    "LEFT OUTER JOIN MPA_RATING ON MPA_RATING.MPA_ID = FILM.mpa_id " +
                    "LEFT OUTER JOIN FILM_DIRECTORS FD ON FILM.film_id = FD.film_id " +
                    "LEFT OUTER JOIN DIRECTORS D ON FD.director_id = D.director_id " +
                    "WHERE LOWER(director_name) LIKE ? OR LOWER(film_name) LIKE ? " +
                    "ORDER BY film_id DESC";
            params.add("%" + query.toLowerCase() + "%");
            params.add("%" + query.toLowerCase() + "%");
        } else if (by.size() == 1) {
            if (by.get(0).equals("title")) {
                sqlQuery = "SELECT FILM.film_id, film_name, FILM.description, release_date, duration, FILM.mpa_id, MPA_RATING.mpa_name, likes, D.director_name " +
                        "FROM FILM " +
                        "LEFT OUTER JOIN MPA_RATING ON MPA_RATING.MPA_ID = FILM.mpa_id " +
                        "LEFT OUTER JOIN FILM_DIRECTORS FD ON FILM.film_id = FD.film_id " +
                        "LEFT OUTER JOIN DIRECTORS D ON FD.director_id = D.director_id " +
                        "WHERE LOWER(film_name) LIKE ? " +
                        "ORDER BY film_id DESC";
                params.add("%" + query.toLowerCase() + "%");
            } else if (by.get(0).equals("director")) {
                sqlQuery = "SELECT FILM.film_id, film_name, FILM.description, release_date, duration, FILM.mpa_id, MPA_RATING.mpa_name, likes, D.director_name " +
                        "FROM FILM " +
                        "LEFT OUTER JOIN MPA_RATING ON MPA_RATING.MPA_ID = FILM.mpa_id " +
                        "LEFT OUTER JOIN FILM_DIRECTORS FD ON FILM.film_id = FD.film_id " +
                        "LEFT OUTER JOIN DIRECTORS D ON FD.director_id = D.director_id " +
                        "WHERE LOWER(director_name) LIKE ? " +
                        "ORDER BY film_id DESC";
                params.add("%" + query.toLowerCase() + "%");
            }
        }

        List<Film> allFilms = this.jdbcTemplate.query(
                sqlQuery,
                params.toArray(),
                (resultSet, rowNum) -> {
                    Film film = new Film();
                    film.setId(resultSet.getInt("film_id"));
                    film.setName(resultSet.getString("film_name"));
                    film.setDescription(resultSet.getString("description"));
                    film.setDuration(resultSet.getInt("duration"));
                    film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());

                    Integer likes = resultSet.getInt("likes");
                    film.setLikes(likes);
                    int mpaId = resultSet.getInt("mpa_id");
                    String mpaName = resultSet.getString("mpa_name");
                    Mpa mpa = new Mpa(mpaId, mpaName);
                    film.setMpa(mpa);
                    return film;
                }
        );

        for (Film film : allFilms) {
            if (film != null) {
                takeGenreFromDb(film);
                takeDirectorsFromDb(film);
            }
        }

        return allFilms;

    }

    @Override
    public List<Film> getFilmsByDirectorId(int directorId, String sortBy) {
        String sqlQuery = "SELECT FILM.film_id, film_name, FILM.description, release_date, duration, FILM.mpa_id, MPA_RATING.mpa_name, likes " +
                "FROM FILM " +
                "INNER JOIN MPA_RATING ON MPA_RATING.MPA_ID = FILM.mpa_id " +
                "INNER JOIN FILM_DIRECTORS FD ON FILM.film_id = FD.film_id " +
                "WHERE DIRECTOR_ID = ?";
        if (sortBy != null) {
            switch (sortBy) {
                case "year":
                    sqlQuery += " ORDER BY EXTRACT(YEAR FROM CAST(release_date AS date)) ASC";
                    break;
                case "likes":
                    sqlQuery += " ORDER BY likes DESC";
                    break;
                default:
                    break;
            }
        }

        List<Film> allFilms = this.jdbcTemplate.query(
                sqlQuery, new Object[]{directorId},
                (resultSet, rowNum) -> {
                    Film film = new Film();
                    film.setId(Integer.parseInt(resultSet.getString("film_id")));
                    film.setName(resultSet.getString("film_name"));
                    film.setDescription(resultSet.getString("description"));
                    film.setDuration(Integer.parseInt(resultSet.getString("duration")));
                    film.setReleaseDate(LocalDate.parse(resultSet.getString("release_date")));

                    String likes = resultSet.getString("likes");
                    if (likes != null) {
                        film.setLikes(Integer.parseInt(resultSet.getString("likes")));
                    }
                    int mpaId = Integer.parseInt(resultSet.getString("mpa_id"));

                    String mpaName = resultSet.getString("mpa_name");
                    Mpa mpa = new Mpa(mpaId, mpaName);
                    film.setMpa(mpa);
                    return film;
                });
        for (Film film : allFilms) {
            if (film != null) {
                takeGenreFromDb(film);
                takeDirectorsFromDb(film);
            }
        }
        return allFilms;
    }

    private Film takeGenreFromDb(Film film) {
        String sqlQuery =
                "select distinct fg.GENRE_ID," +
                        " g.GENRE_NAME from FILM_GENRES fg " +
                        "inner join GENRE g on fg.GENRE_ID = g.GENRE_ID " +
                        "where fg.FILM_ID = ?";
        List<Genres> foundGenres = jdbcTemplate.query(
                sqlQuery,
                new Object[]{film.getId()},
                (resultSet, rowNum) -> {
                    Genres genres = new Genres();
                    genres.setId(Integer.parseInt(resultSet.getString("genre_id")));
                    genres.setName(resultSet.getString("genre_name"));
                    return genres;
                });

        film.setGenres(foundGenres);
        return film;
    }

    private Film takeDirectorsFromDb(Film film) {
        String sqlQuery =
                "select fd.DIRECTOR_ID," +
                        " d.DIRECTOR_NAME from FILM_DIRECTORS fd " +
                        "inner join DIRECTORS d on fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                        "where fd.FILM_ID = ?";
        List<Director> foundDirectors = jdbcTemplate.query(
                sqlQuery,
                new Object[]{film.getId()},
                (resultSet, rowNum) -> {
                    Director director = new Director();
                    director.setId(Integer.parseInt(resultSet.getString("director_id")));
                    director.setName(resultSet.getString("director_name"));
                    return director;
                });

        film.setDirectors(foundDirectors);
        return film;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        Map<Integer, Film> filmMap = new HashMap<>();

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film");

        int mpaId = 0;
        String mpaName = "";

        if (filmRows.next()) {
            do {
                SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa_rating where mpa_id = ?", filmRows.getInt("mpa_id"));

                SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select distinct f.film_id, fg.genre_id, g.genre_name " +
                        "from film_genres fg join genre g on fg.genre_id = g.genre_id " +
                        "join film f on fg.film_id = f.film_id where f.film_id = ?", filmRows.getInt("film_id"));

                do {
                    if (mpaRows.next()) {
                        mpaId = mpaRows.getInt("mpa_id");
                        mpaName = mpaRows.getString("mpa_name");
                    }
                } while (mpaId != mpaRows.getInt("mpa_id"));

                Genres genre = new Genres();
                List<Genres> genresList = new ArrayList<>();
                int genreId = 0;
                String genreName = "";

                if (genreRows.next()) {
                    genreId = genreRows.getInt("genre_id");
                    genreName = genreRows.getString("genre_name");
                }

                Film film = new Film();
                Mpa mpa = new Mpa();
                mpa.setId(mpaId);
                mpa.setName(mpaName);

                if (genreId > 0) {
                    genre.setId(genreId);
                    genre.setName(genreName);
                    genresList.add(genre);
                }

                film.setId(Integer.parseInt(filmRows.getString("film_id")));
                film.setName(filmRows.getString("film_name"));
                film.setDescription(filmRows.getString("description"));
                film.setReleaseDate(LocalDate.parse(filmRows.getString("release_date")));
                film.setDuration(filmRows.getLong("duration"));
                film.setMpa(mpa);
                film.setLikes(filmRows.getInt("likes"));

                // добавляем директора в фильм
                takeDirectorsFromDb(film);
                //  добавления всех жанров
                takeGenreFromDb(film);
                filmMap.put(film.getId(), film);
            } while (filmRows.next());
        }
        return filmMap;
    }

    @Override
    public Film getFilmById(Integer id) throws IllegalArgumentException {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa_rating");
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where film_id = ?", id);
        Film film = new Film();

        SqlRowSet isGenresRowsFound = jdbcTemplate.queryForRowSet("select * from film_genres where film_id = ?", id);

        if (isGenresRowsFound.next()) {
            List<Integer> genreIdArray = new ArrayList<>();
            do {
                int idGenreTmp = isGenresRowsFound.getInt("genre_id");
                if (!genreIdArray.contains(idGenreTmp)) {
                    genreIdArray.add(idGenreTmp);
                }
            } while (isGenresRowsFound.next());

            Map<Integer, String> genreMap = new HashMap<>();
            List<Genres> newGenresList = new ArrayList<>();

            SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from genre");
            if (genresRows.next()) {
                do {
                    int genreIdtmp = genresRows.getInt("genre_id");
                    String genreNameTmp = genresRows.getString("genre_name");
                    genreMap.put(genreIdtmp, genreNameTmp);
                } while (genresRows.next());
            }

            for (int i = 0; i < genreIdArray.size(); i++) {
                Genres genre = new Genres();

                int genreIdTmp = genreIdArray.get(i);
                genre.setId(genreIdTmp);

                for (Map.Entry<Integer, String> integerStringEntry : genreMap.entrySet()) {
                    if (integerStringEntry.getKey() == genreIdTmp) {
                        genre.setName(integerStringEntry.getValue());
                    }
                }
                newGenresList.add(genre);
            }
            film.setGenres(newGenresList);
        }

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
        // Добавляем директора
        takeDirectorsFromDb(film);
        return film;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        List<Genres> genres = film.getGenres();
        List<Integer> genreId = new ArrayList<>();

        if (genres.size() > 0) {
            for (Genres genre : genres) {
                genreId.add(genre.getId());
            }

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

        if (genres.size() > 0) {
            for (int i = 0; i < genreId.size(); i++) {
                String sqlQueryGenres = "insert into film_genres (film_id, genre_id) " +
                        "values (?, ?)";
                jdbcTemplate.update(sqlQueryGenres,
                        keyHolder.getKey().intValue(),
                        genreId.get(i));
            }
        }

        // добавляем функционал добавления нового директора
        List<Director> directors = film.getDirectors().stream().distinct().collect(Collectors.toList());

        String sqlQueryDirectors = "insert into FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) " +
                "values (?, ?)";
        jdbcTemplate.batchUpdate(sqlQueryDirectors, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Director director = directors.get(i);
                ps.setLong(1, film.getId());
                ps.setLong(2, director.getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });

        film.setDirectors(directors);

        return film;
    }

    @Override
    public Film updateFilm(Film film) throws IllegalArgumentException {
        String queryDeleteDirector = "delete from FILM_DIRECTORS where FILM_ID = ?";
        jdbcTemplate.update(queryDeleteDirector, film.getId());
        String queryDeleteGenre = "delete from FILM_GENRES where FILM_ID = ?";
        jdbcTemplate.update(queryDeleteGenre, film.getId());

        //    SqlRowSet getLastIdRows = jdbcTemplate.queryForRowSet("select * from film order by film_id desc limit 1");
        int genreId = 0;
        Map<Integer, String> genreMap = new HashMap<>();

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from genre");
        if (genresRows.next()) {
            do {
                int genreIdtmp = genresRows.getInt("genre_id");
                String genreNameTmp = genresRows.getString("genre_name");
                genreMap.put(genreIdtmp, genreNameTmp);
            } while (genresRows.next());
        }

        List<Genres> genresList = film.getGenres();
        if (genresList.size() > 0) {
            List<Integer> genreIdArray = new ArrayList<>();
            List<Genres> newGenresList = new ArrayList<>();
            for (Genres genre : genresList) {
                genreId = genre.getId();
                if (!genreIdArray.contains(genreId)) {
                    genreIdArray.add(genreId);
                }
            }

            for (int i = 0; i < genreIdArray.size(); i++) {
                int id = genreIdArray.get(i);
                String name = "";

                for (Map.Entry<Integer, String> integerStringEntry : genreMap.entrySet()) {
                    if (id == integerStringEntry.getKey()) {
                        name = integerStringEntry.getValue();
                        break;
                    }
                }
                Genres newGenre = new Genres();
                newGenre.setId(id);
                newGenre.setName(name);
                newGenresList.add(newGenre);

                String sqlQuery = "delete from film_genres where film_id = ?";
                jdbcTemplate.update(sqlQuery, film.getId());


                for (Genres genres : newGenresList) {
                    String sqlQueryGenres = "insert into film_genres (film_id, genre_id) " +
                            "values (?, ?)";
                    jdbcTemplate.update(sqlQueryGenres,
                            film.getId(),
                            genres.getId());
                }
            }

            film.setGenres(newGenresList);

            if (genresList.size() > 0) {
                String sqlQueryGenres = "insert into film_genres (film_id, genre_id) " +
                        "values (?, ?)";
                jdbcTemplate.update(sqlQueryGenres,
                        film.getId(),
                        genreId);
            }
        } else {
            SqlRowSet getFilmIdGenresRows = jdbcTemplate.queryForRowSet("select * from film_genres where film_id = ?", film.getId());

            if (getFilmIdGenresRows.next()) {
                int filmId = getFilmIdGenresRows.getInt("film_id");

                if (filmId > 0) {
                    String sqlQuery = "delete from film_genres where film_id = ?";
                    jdbcTemplate.update(sqlQuery, filmId);
                }
            }
        }

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where film_id = ?", film.getId());

        if (!filmRows.next()) {
            throw new IllegalArgumentException("film id not found");
        }

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select mpa_name from mpa_rating where mpa_id = ?",
                film.getMpa().getId());

        Mpa mpa = new Mpa();
        mpa.setId(film.getMpa().getId());
        film.setMpa(mpa);

        if (mpaRows.next()) {
            mpa.setName(mpaRows.getString("mpa_name"));
        }

        String sqlQuery = "update film set " +
                "film_name = ?, release_date = ?, description = ?, duration = ?, likes = ?, mpa_id = ?" +
                " where film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());


        //add director
        String queryDelete = "delete from FILM_DIRECTORS where FILM_ID = ?";
        jdbcTemplate.update(queryDelete, film.getId());

        List<Director> directors = film.getDirectors().stream().distinct().collect(Collectors.toList());

        String sqlQueryDirectors = "insert into FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) " +
                "values (?, ?)";
        jdbcTemplate.batchUpdate(sqlQueryDirectors, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Director director = directors.get(i);
                ps.setLong(1, film.getId());
                ps.setLong(2, director.getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });

        film.setDirectors(directors);
        return film;
    }

    @Override
    public Film deleteFilm(Film film) {
        String sqlQuery = "delete from film where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        return film;
    }


    @Override
    public boolean deleteFilmById(Integer id) throws IllegalArgumentException {
        boolean isFilmDelete = false;

        String sqlQueryOne = "delete from film where film_id = ?";
        int statusOne = jdbcTemplate.update(sqlQueryOne, id);

        String sqlQueryTwo = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sqlQueryTwo, id);

        if (statusOne == 1) {
            isFilmDelete = true;
        } else {
            throw new IllegalArgumentException("film id not found");
        }

        return isFilmDelete;
    }

    @Override
    public List<Film> getFilmsSortedByPopularity() {
        String sql = "SELECT F.FILM_ID, " +
                "       F.FILM_NAME, " +
                "       F.DESCRIPTION, " +
                "       F.RELEASE_DATE, " +
                "       F.DURATION, " +
                "       M.MPA_ID AS MPA_ID, " +
                "       M.MPA_NAME AS MPA_NAME, " +
                "       F.LIKES " +
                "FROM FILM F " +
                "         INNER JOIN MPA_RATING M ON F.MPA_ID = M.MPA_ID " +
                "         LEFT JOIN FILM_LIKES FL ON F.FILM_ID = FL.FILM_ID " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(FL.FILM_ID) DESC";

        return jdbcTemplate.query(sql, new FilmMaker(jdbcTemplate));
    }
}