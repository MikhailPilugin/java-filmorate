package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class FilmMaker implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("FILM_ID");

        String sql = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID=?";
        List<Integer> listLikes = jdbcTemplate.query(sql,
                (rs1, rowNum1) -> rs1.getInt("USER_ID"), id);

        Set<Integer> likes = new HashSet<>(listLikes);

        sql = "SELECT G.GENRE_ID ID, G.GENRE_NAME FROM GENRE G " +
                "INNER JOIN FILM_GENRES FG ON G.GENRE_ID = FG.GENRE_ID " +
                "WHERE FG.FILM_ID=?";
        List<Genres> genres = jdbcTemplate.query(sql, new Object[]{id},
                new BeanPropertyRowMapper<>(Genres.class));
        Mpa mpa = new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME"));

        return new Film(rs.getInt("FILM_ID"),
                rs.getString("FILM_NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getLong("DURATION"),
                likes,
                rs.getInt("LIKES"),
                mpa,
                genres);
    }
}

