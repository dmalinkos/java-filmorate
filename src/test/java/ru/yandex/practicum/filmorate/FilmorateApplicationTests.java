package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserStorage userStorage;
	private final FilmStorage filmStorage;
	private final MpaDao mpaDao;
	private final GenreDao genreDao;
	private final EventDao eventDao;

	@Test
	@Sql(scripts = "file:src/test/java/ru/yandex/practicum/filmorate/testResources/dataTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = "file:src/test/java/ru/yandex/practicum/filmorate/testResources/deleteData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void testFindUserById() {
		Optional<User> userOptional = Optional.of(userStorage.findById(1L));
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L))
				.hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("email", "user@ya.ru"))
				.hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("login", "login"))
				.hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("name", "username"))
				.hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1998,8,27))
				);
	}

	@Test
	@Sql(scripts = "file:src/test/java/ru/yandex/practicum/filmorate/testResources/dataTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = "file:src/test/java/ru/yandex/practicum/filmorate/testResources/deleteData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void testFindFilmById() {
		Optional<Film> filmOptional = Optional.of(filmStorage.findById(1L));
		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("id", 1L))
				.hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("name", "ДМБ"))
				.hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("description", "Описание"))
				.hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("duration", 85L))
				.hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000,3,31))
				);
	}

	@Test
	void testFindGenreById() {
		Optional<Genre> genreOptional = Optional.of(genreDao.findById(1));
		assertThat(genreOptional)
				.isPresent()
				.hasValueSatisfying(genre -> assertThat(genre).hasFieldOrPropertyWithValue("id", 1))
				.hasValueSatisfying(genre -> assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия")
				);
	}

	@Test
	void testFindMpaById() {
		Optional<Mpa> mpaOptional = Optional.of(mpaDao.findById(1));
		assertThat(mpaOptional)
				.isPresent()
				.hasValueSatisfying(mpa -> assertThat(mpa).hasFieldOrPropertyWithValue("id", 1))
				.hasValueSatisfying(mpa -> assertThat(mpa).hasFieldOrPropertyWithValue("name", "G")
				);
	}

	@Test
	@Sql(scripts = "file:src/test/java/ru/yandex/practicum/filmorate/testResources/dataTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	void testFindFeedByUserId() {
		Optional<ArrayList<Event>> feed = Optional.of(new ArrayList<>(eventDao.findAllByUserId(1L)));
		assertThat(feed)
				.isPresent()
				.hasValueSatisfying(event -> assertThat(event.get(0)).hasFieldOrPropertyWithValue("eventId", 1L))
				.hasValueSatisfying(event -> assertThat(event.get(0)).hasFieldOrPropertyWithValue("userId", 1L))
				.hasValueSatisfying(event -> assertThat(event.get(0)).hasFieldOrPropertyWithValue("timestamp", 1670590017281L))
				.hasValueSatisfying(event -> assertThat(event.get(0)).hasFieldOrPropertyWithValue("eventType", EventType.FRIEND))
				.hasValueSatisfying(event -> assertThat(event.get(0)).hasFieldOrPropertyWithValue("operation", Operation.ADD))
				.hasValueSatisfying(event -> assertThat(event.get(0)).hasFieldOrPropertyWithValue("entityId", 2L)
				);
	}
}
