INSERT INTO users (login, user_name, birthday, email) VALUES ('ser', 'sergey', '1990-01-01', 'sergey@mail.ru');


INSERT INTO users (login, user_name, birthday, email) VALUES ('pet', 'petr', '1990-02-01', 'petr@mail.ru');

INSERT INTO users (login, user_name, birthday, email) VALUES ('pet', 'petr', '1990-02-01', 'petr@mail.ru');
INSERT INTO users (login, user_name, birthday, email) VALUES ('pet', 'petr', '1990-02-01', 'petr@mail.ru');
INSERT INTO users (login, user_name, birthday, email) VALUES ('pet', 'petr', '1990-02-01', 'petr@mail.ru');
INSERT INTO users (login, user_name, birthday, email) VALUES ('pet', 'petr', '1990-02-01', 'petr@mail.ru');
INSERT INTO users (login, user_name, birthday, email) VALUES ('pet', 'petr', '1990-02-01', 'petr@mail.ru');
INSERT INTO users (login, user_name, birthday, email) VALUES ('pet', 'petr', '1990-02-01', 'petr@mail.ru');
INSERT INTO users (login, user_name, birthday, email) VALUES ('pet', 'petr', '1990-02-01', 'petr@mail.ru');
INSERT INTO users (login, user_name, birthday, email) VALUES ('pet', 'petr', '1990-02-01', 'petr@mail.ru');
INSERT INTO users (login, user_name, birthday, email) VALUES ('pet', 'petr', '1990-02-01', 'petr@mail.ru');
INSERT INTO users (login, user_name, birthday, email) VALUES ('pet', 'petr', '1990-02-01', 'petr@mail.ru');


INSERT INTO friendship (user_id, friend_id, status) VALUES ('1', '2', 'UNCONFIRMED');
INSERT INTO friendship (user_id, friend_id, status) VALUES ('2', '1', 'UNCONFIRMED');

INSERT INTO friendship (user_id, friend_id, status) VALUES ('3', '1', 'UNCONFIRMED');
INSERT INTO friendship (user_id, friend_id, status) VALUES ('1', '3', 'UNCONFIRMED');
INSERT INTO friendship (user_id, friend_id, status) VALUES ('2', '3', 'UNCONFIRMED');
INSERT INTO friendship (user_id, friend_id, status) VALUES ('3', '2', 'UNCONFIRMED');
INSERT INTO friendship (user_id, friend_id, status) VALUES ('4', '1', 'UNCONFIRMED');
INSERT INTO friendship (user_id, friend_id, status) VALUES ('1', '4', 'UNCONFIRMED');

UPDATE friendship SET status = 'CONFIRMED' WHERE user_id = '1' AND friend_id = '2';

UPDATE friendship SET status = 'CONFIRMED' WHERE user_id = '2' AND friend_id = '1';

UPDATE friendship SET status = 'CONFIRMED' WHERE user_id = '3' AND friend_id = '1';

UPDATE friendship SET status = 'CONFIRMED' WHERE user_id = '1' AND friend_id = '3';

UPDATE friendship SET status = 'CONFIRMED' WHERE user_id = '2' AND friend_id = '3';

INSERT INTO genre (genre_name) VALUES ('COMEDY');
INSERT INTO genre (genre_name) VALUES ('DRAMA');
INSERT INTO genre (genre_name) VALUES ('CARTOON');
INSERT INTO genre (genre_name) VALUES ('THRILLER');
INSERT INTO genre (genre_name) VALUES ('DOCUMENTARY');
INSERT INTO genre (genre_name) VALUES ('ACTION');


INSERT INTO mpa_rating (mpa_name, description) VALUES ('G', 'у фильма нет возрастных ограничений');

INSERT INTO mpa_rating (mpa_name, description) VALUES ('PG', 'детям рекомендуется смотреть фильм с родителями');

INSERT INTO mpa_rating (mpa_name, description) VALUES ('PG-13', 'детям до 13 лет просмотр не желателен');

INSERT INTO mpa_rating (mpa_name, description) VALUES ('R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого');

INSERT INTO mpa_rating (mpa_name, description) VALUES ('NC-17', 'лицам до 18 лет просмотр запрещён');


INSERT INTO film (film_name, description, release_date, duration, mpa_id, likes) VALUES ('Kino 1', 'Horoshee kino 1', '2022-01-01', '120', '1', '0');

INSERT INTO film (film_name, description, release_date, duration, mpa_id, likes) VALUES ('Kino 2', 'Horoshee kino 2', '2022-02-01', '121', '2', '0');

INSERT INTO film (film_name, description, release_date, duration, mpa_id, likes) VALUES ('Kino 3', 'Horoshee kino 3', '2022-03-01', '122', '3', '0');

INSERT INTO film (film_name, description, release_date, duration, mpa_id, likes) VALUES ('Kino 4', 'Horoshee kino 4', '2022-04-01', '123', '4', '0');

INSERT INTO film (film_name, description, release_date, duration, mpa_id, likes) VALUES ('Kino 5', 'Horoshee kino 5', '2022-05-01', '124', '5', '0');


INSERT INTO film_genres (film_id, genre_id) VALUES ('1', '1');
INSERT INTO film_genres (film_id, genre_id) VALUES ('2', '1');
INSERT INTO film_genres (film_id, genre_id) VALUES ('3', '1');
INSERT INTO film_genres (film_id, genre_id) VALUES ('4', '1');
INSERT INTO film_genres (film_id, genre_id) VALUES ('5', '1');
INSERT INTO film_genres (film_id, genre_id) VALUES ('1', '2');



INSERT INTO film_likes (film_id, user_id) VALUES ('2', '1');
UPDATE film SET likes = likes + 1 WHERE film_id = '2';

INSERT INTO film_likes (film_id, user_id) VALUES ('2', '2');
UPDATE film SET likes = likes + 1 WHERE film_id = '2';

INSERT INTO film_likes (film_id, user_id) VALUES ('3', '3');
UPDATE film SET likes = likes + 1 WHERE film_id = '3';

INSERT INTO film_likes (film_id, user_id) VALUES ('3', '4');
UPDATE film SET likes = likes + 1 WHERE film_id = '3';

INSERT INTO film_likes (film_id, user_id) VALUES ('3', '5');
UPDATE film SET likes = likes + 1 WHERE film_id = '3';

INSERT INTO film_likes (film_id, user_id) VALUES ('2', '1');
UPDATE film SET likes = likes + 1 WHERE film_id = '2';


INSERT INTO film_likes (film_id, user_id) VALUES ('1', '1');
UPDATE film SET likes = likes + 1 WHERE film_id = '1';

INSERT INTO film_likes (film_id, user_id) VALUES ('4', '1');
UPDATE film SET likes = likes + 1 WHERE film_id = '4';

INSERT INTO film_likes (film_id, user_id) VALUES ('5', '1');
UPDATE film SET likes = likes + 1 WHERE film_id = '5';

INSERT INTO film_likes (film_id, user_id) VALUES ('5', '1');
UPDATE film SET likes = likes + 1 WHERE film_id = '5';