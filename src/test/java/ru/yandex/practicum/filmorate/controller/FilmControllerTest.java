package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.typeAdapter.LocalDateAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Test
    public void invalidName() throws IOException, InterruptedException {
        Film film = Film.builder()
                .name("")
                .description("Description")
                .duration(200)
                .releaseDate(LocalDate.of(1900, 3, 25))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(film), UTF_8))
                .uri((URI.create("http://localhost:8080/films/")))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
    }

    @Test
    public void invalidDescription() throws IOException, InterruptedException {
        String badDescription = "1".repeat(201);

        Film film = Film.builder()
                .name("name")
                .description(badDescription)
                .duration(120)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(film), UTF_8))
                .uri((URI.create("http://localhost:8080/films/")))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
    }

    @Test
    public void invalidReleaseDate() throws IOException, InterruptedException {
        Film film = Film.builder()
                .name("name")
                .description("Description")
                .duration(200)
                .releaseDate(LocalDate.of(1800, 3, 25))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(film), UTF_8))
                .uri((URI.create("http://localhost:8080/films/")))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
    }

    @Test
    public void invalidDuration() throws IOException, InterruptedException {
        Film film = Film.builder()
                .name("name")
                .description("Description")
                .duration(-10)
                .releaseDate(LocalDate.of(1900, 3, 25))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(film), UTF_8))
                .uri((URI.create("http://localhost:8080/films/")))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
    }

}

