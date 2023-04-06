SET MODE PostgreSQL;

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS MPA CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS friendships CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS review_rates CASCADE;

CREATE TABLE IF NOT EXISTS users (
    user_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_email varchar(100) UNIQUE NOT NULL,
    user_login varchar(100) UNIQUE NOT NULL,
    user_name varchar(100) NOT NULL,
    user_birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS MPA (
    mpa_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS friendships (
    friendship_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id int REFERENCES users (user_id),
    friend_id int REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS films (
    film_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name varchar(255) NOT NULL,
    film_description varchar(200) NOT NULL,
    film_releaseDate date NOT NULL ,
    film_duration int CHECK (film_duration > 0) NOT NULL,
    mpa_id int REFERENCES MPA (mpa_id)
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id int REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id int REFERENCES genres (genre_id),
    CONSTRAINT fg_pk PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
    user_id int REFERENCES users (user_id),
    film_id int REFERENCES films (film_id),
    CONSTRAINT likes_pk PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR NOT NULL,
    user_id int REFERENCES users (user_id),
    is_positive BOOLEAN NOT NULL,
    film_id int REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS review_rates (
    review_id BIGINT REFERENCES reviews (review_id) ON DELETE CASCADE,
    rated_by_id int REFERENCES users (user_id) ON DELETE CASCADE,
    rate_value int,
    CONSTRAINT review_rates_pk PRIMARY KEY (review_id, rated_by_id),
    CONSTRAINT value_chk CHECK ( -1 <= rate_value AND rate_value <= 1)
);