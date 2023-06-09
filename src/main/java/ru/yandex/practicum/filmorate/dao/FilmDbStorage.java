package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

                SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select f.film_id, fg.genre_id, g.genre_name " +
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
                    film.setGenres(genresList);
                }


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

        return film;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        List<Genres> genres = film.getGenres();
        int genreId = 0;

        if (genres.size() > 0) {
            for (Genres genre : genres) {
                genreId = genre.getId();
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
            String sqlQueryGenres = "insert into film_genres (film_id, genre_id) " +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQueryGenres,
                    keyHolder.getKey().intValue(),
                    genreId);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws IllegalArgumentException {
        SqlRowSet getLastIdRows = jdbcTemplate.queryForRowSet("select * from film order by film_id desc limit 1");
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

        return film;
    }

    @Override
    public Film deleteFilm(Film film) throws ValidationException {
        String sqlQuery = "delete from film where id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        return film;
    }
}