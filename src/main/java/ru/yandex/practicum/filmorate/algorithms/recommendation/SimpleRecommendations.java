package ru.yandex.practicum.filmorate.algorithms.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleRecommendations {
    private final JdbcTemplate jdbcTemplate;

    private final FilmStorage filmStorage;

    public List<Film> getListOfRecommendedFilms(Long id) {

        List<Like> tableOfLikes = jdbcTemplate.query("SELECT * FROM likes", this::mapRowToLike);
        HashMap<Long, List<Long>> collectionOfLikes = makeCollectionOfLike(tableOfLikes);
        List<Long> listOfRecommendations = makeRecommendationsList(collectionOfLikes, id);

        if (listOfRecommendations.size() > 0) {
            return filmStorage.getListFilms(listOfRecommendations);
        }

        return new ArrayList<>();
    }

    private HashMap<Long, List<Long>> makeCollectionOfLike(List<Like> likesList) {
        HashMap<Long, List<Long>> collectionOfLikes = new HashMap<>();

        for (var like : likesList) {
            Long userId = like.getUserId();
            if (!collectionOfLikes.containsKey(userId)) {
                collectionOfLikes.put(userId, new ArrayList<>());
            }

            var l1 = collectionOfLikes.get(userId);

            l1.add(like.getFilmId());
        }
        return collectionOfLikes;
    }

    private List<Long> makeRecommendationsList(HashMap<Long, List<Long>> aggLikes, Long userId) {

        long count;
        long maxCount = 0L;
        Long maxCountUserId = userId;

        var l1 = aggLikes.get(userId);

        if (l1 == null) {
            return new ArrayList<>();
        }

        for(var e2 : aggLikes.entrySet()) {
            if (!e2.getKey().equals(userId)) {
                count = l1.stream().filter(i -> e2.getValue().stream().anyMatch(n -> Objects.equals(n, i))).count();
                if (count >= maxCount) {
                    maxCount = count;
                    maxCountUserId = e2.getKey();
                }
            }
        }

        if (!Objects.equals(maxCountUserId, userId)) {
            var l2 = aggLikes.get(maxCountUserId);
            return l2.stream().filter(i -> l1.stream().noneMatch(n -> Objects.equals(n, i))).collect(Collectors.toList());
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
