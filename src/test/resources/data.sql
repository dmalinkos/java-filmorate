SET MODE PostgreSQL;

INSERT INTO
    MPA(MPA_ID, MPA_NAME)
VALUES (1,'G'),
       (2,'PG'),
       (3,'PG-13'),
       (4,'R'),
       (5,'NC-17')
    ON CONFLICT DO NOTHING;

INSERT INTO
    GENRES(GENRE_ID, GENRE_NAME)
VALUES (1,'Комедия'),
       (2,'Драма'),
       (3,'Мультфильм'),
       (4,'Триллер'),
       (5,'Документальный'),
       (6,'Боевик')
    ON CONFLICT DO NOTHING;

INSERT INTO users (user_email, user_login, user_name, user_birthday)
VALUES
('mail1@mail.ru', 'login1', 'name1', PARSEDATETIME('2000-01-01', 'yyyy-MM-dd')),
('second@mail.ru', 'second', 'name2', PARSEDATETIME('2001-01-01', 'yyyy-MM-dd')),
('user3@ya.ru', 'login3', 'username3', '1998-08-27'),
('user4@gmail.ru', 'login4', 'username4', '1998-01-04');

INSERT INTO films (film_name, film_description, film_releaseDate, film_duration)
VALUES ('nisi eiusmod', 'adipisicing', PARSEDATETIME('1967-03-25', 'yyyy-MM-dd'), 100);

INSERT INTO films (film_name, film_description, film_releaseDate, film_duration, mpa_id)
VALUES ('New film', 'New film about friends', PARSEDATETIME('1999-04-30', 'yyyy-MM-dd'), 120, 3);