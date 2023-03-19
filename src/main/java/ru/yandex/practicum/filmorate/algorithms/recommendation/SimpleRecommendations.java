package ru.yandex.practicum.filmorate.algorithms.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализовать простую рекомендательную систему для фильмов. Примерный алгоритм выглядит следующим образом:
 * 1. Найти пользователей с максимальным количеством пересечения по лайкам.
 * 2. Определить фильмы, которые один пролайкал, а другой нет.
 * 3. Рекомендовать фильмы, которым поставил лайк пользователь с похожими вкусами, а тот, для кого составляется
 *    рекомендация, ещё не поставил.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleRecommendations {
    private final JdbcTemplate jdbcTemplate;

    private final FilmStorage filmStorage;

    public List<Film> getFilmLikesTable(Long id) {

        List<Like> likesTable = jdbcTemplate.query("SELECT * FROM likes", this::mapRowToLike);
        HashMap<Long, List<Long>> aggLikes = collectionOfLikes(likesTable);
        List<Long> intercept = removeInterception(aggLikes, id);

        if (intercept.size() > 0) {
            return filmStorage.getListFilms(intercept);
        }

        return new ArrayList<>();
    }

    private HashMap<Long, List<Long>> collectionOfLikes(List<Like> likesList) {
        HashMap<Long, List<Long>> collLikes = new HashMap<>();

        for (var like : likesList) {
            Long userId = like.getUserId();
            if (!collLikes.containsKey(userId)) {
                collLikes.put(userId, new ArrayList<>());
            }
            var l1 = collLikes.get(userId);

            if (l1 == null) {
                throw new EntityNotExistException(
                        String.format("collectionOfLikes. Ошибка получения списка лайков пользователя с userId = %d", userId));
            }

            l1.add(like.getFilmId());
        }
        return collLikes;
    }

    private List<Long> removeInterception(HashMap<Long, List<Long>> aggLikes, Long userId) {

        Long count;
        Long maxCount = 0L;
        Long maxCountUserId = userId;

        var l1 = aggLikes.get(userId);

        if (l1 == null) {
            return new ArrayList<>();
        }

        for(var e2 : aggLikes.entrySet()) {
            if (!e2.getKey().equals(userId)) {
                count = l1.stream().filter(i -> e2.getValue().stream().anyMatch(n -> n == i)).count();
                if (count >= maxCount) {
                    maxCount = count;
                    maxCountUserId = e2.getKey();
                }
            }
        }

        if (maxCountUserId > 0) {
            var l2 = aggLikes.get(maxCountUserId);
            if (l2 == null) {
                return new ArrayList<>();
            }
            return l2.stream().filter(i -> l1.stream().noneMatch(n -> n == i)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private Like mapRowToLike(ResultSet rs, int rowNum) throws SQLException {
        Long filmId = rs.getLong("FILM_ID");
        Long userId = rs.getLong("USER_ID");
        return Like.builder()
                .filmId(filmId)
                .userId(userId)
                .build();
    }
}
