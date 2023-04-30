# Схема БД приложения Filmorate


![Схема БД приложения Filmorate](documents/DB_filmorate_scheme_.png)

# Описание таблиц:

## Users - содержит данные о пользователях

* **user_id** - индентификатор пользователя, первичный ключ
* **login** - логин пользователя
* **first_name** - имя пользователя
* **last_name** - фамилия пользователя
* **birthday** - день рождения пользователя
* **email** - адрес почты пользователя


## Friendship - сосдержит данные о статусах добавления в друзья

* **(user_id, friend_id)** - индентификаторы пользователя, составной первичный ключ (формируется из двух уникальных user_id из таблицы Users)
* **status** - статус добавления в друзья (UNCONFIRMED, CONFIRMED)


## Genre - содержит данные о жанрах фильмов

* **genre_id** - идентификатор жанра фильма, первичный ключ
* **name** - название жанра фильма


## Film - содержит данные о фильмах

* **film_id** - идентификатор фильма, первичный ключ
* **genre_id** - идентификатор жанра фильма, внешний ключ
* **name** - название фильма
* **description** - краткое описание фильма
* **release_date** - дата премьеры фильма
* **duration** - продолжительность фильма
* **mpa_rating** - возрастной рейтинг. Например:
G — у фильма нет возрастных ограничений,
PG — детям рекомендуется смотреть фильм с родителями,
PG-13 — детям до 13 лет просмотр не желателен,
R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
NC-17 — лицам до 18 лет просмотр запрещён.


## Likes - содержит данные о пользовательском рейтинге фильма

* **(user_id, film_id)** - составной первичный ключ (формируется из  user_id таблицы Users и film_id из таблицы Film)
* **rate** - рейтинг фильма, составленный из количества лайков пользователей


## Пример запросов к БД

INSERT INTO users VALUES ('1','ser', 'sergey', 'sergeev', '1990-01-01', 'sergey@mail.ru');
INSERT INTO users VALUES ('2','pet', 'petr', 'petrov', '1990-02-01', 'petr@mail.ru');

INSERT INTO friendship VALUES ('1', '2', 'UNCONFIRMED');
INSERT INTO friendship VALUES ('2', '1', 'UNCONFIRMED');

UPDATE friendship
SET status = 'CONFIRMED'
WHERE user_id = '1' AND friend_id = '2';

SELECT *
FROM users;

SELECT *
FROM friendship;

INSERT INTO genre VALUES ('2', 'DRAMA');
INSERT INTO genre VALUES ('3', 'CARTOON');
INSERT INTO genre VALUES ('4', 'THRILLER');
INSERT INTO genre VALUES ('5', 'DOCUMENTARY');
INSERT INTO genre VALUES ('6', 'ACTION');

SELECT *
FROM genre;

INSERT INTO film VALUES ('1', '1', 'Kino 1', 'Horoshee kino', '2022-01-01', '120', 'G');

SELECT *
FROM film;

INSERT INTO likes VALUES ('1', '1', '0');

SELECT *
FROM likes;