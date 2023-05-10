DROP TABLE IF EXISTS users, friendship, genre, mpa_rating, film, film_genres, film_likes CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    login VARCHAR,
    user_name VARCHAR,
    birthday DATE,
    email VARCHAR
    );


CREATE TABLE IF NOT EXISTS friendship (
    user_id INTEGER NOT NULL REFERENCES users(user_id),
    friend_id INTEGER NOT NULL REFERENCES users(user_id),
    status VARCHAR
    );


CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR
    );


CREATE TABLE IF NOT EXISTS mpa_rating (
    mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name VARCHAR,
    description VARCHAR
    );


CREATE TABLE IF NOT EXISTS film (
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name VARCHAR,
    description VARCHAR,
    release_date DATE,
    duration INT8,
    mpa_id INTEGER REFERENCES mpa_rating (mpa_id),
    likes INTEGER
    );


CREATE TABLE IF NOT EXISTS film_genres (
    film_id INTEGER REFERENCES film (film_id),
    genre_id INTEGER REFERENCES genre (genre_id)
    );


CREATE TABLE IF NOT EXISTS film_likes (
    user_id INTEGER REFERENCES users (user_id),
    film_id INTEGER REFERENCES film (film_id)
    );