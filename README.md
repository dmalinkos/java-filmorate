# DB Scheme

![Untitled (2)](H:\NewSkills\Java\YP\sp12\fp\db-diag.png)

<details><summary><h3>https://dbdiagram.io/d/64197b41296d97641d898bb0</h3></summary>

```
Table users {
user_id int [pk]
user_email varchar(100) [not null, unique]
user_login varchar(100) [not null, unique]
user_name varchar(100) [not null]
user_birthday date [not null]
}

Table MPA {
mpa_id int [pk]
mpa_name varchar(100) [not null]
}


Table friendships {
friendship_id int [pk]
user_id int [ref: > users.user_id]
friend_id int [ref: > users.user_id]
}

Table films {
film_id int [pk]
film_name varchar(255) [not null]
film_description varchar(200) [not null]
film_releaseDate date [not null]
film_duration int [not null]
mpa_id int [ref: - MPA.mpa_id]
}

Table genres{
genre_id int [pk]
genre_name varchar(50) [not null]
}

Table film_genres {
film_id int [ref: > films.film_id]
genre_id int [ref: > genres.genre_id]
}

Table directors {
director_id int [pk]
director_name varchar(50) [not null]
}

Table film_directors {
film_id int [ref: > films.film_id]
director_id int [ref: > directors.director_id]
}

Table likes {
user_id int [ref: > users.user_id]
film_id int [ref: > films.film_id]
}


Table reviews {
review_id BIGINT [pk]
content VARCHAR [not null]
user_id int [ref: > users.user_id]
is_positive boolean [not null]
film_id int [ref: > films.film_id]
}

Table review_rates {
review_id BIGINT [ref: > reviews.review_id]
rated_by_id int [ref: > users.user_id]
rate_value int
}

Table events {
event_id int [pk]
user_id int [ref: > users.user_id]
time_stamp bigint [not null]
event_type varchar(6) [not null]
operation varchar(6) [not null]
entity_id int [not null]
}
```
</details>