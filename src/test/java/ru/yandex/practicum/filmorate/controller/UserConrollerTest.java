package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.typeAdapter.LocalDateAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserConrollerTest {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Test
    public void invalidLogin() throws IOException, InterruptedException {
        User user = User.builder()
                .login("")
                .email("user@ya.ru")
                .name("Name")
                .birthday(LocalDate.of(1998, 1,1))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user), UTF_8))
                .uri((URI.create("http://localhost:8080/users/")))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
    }

    @Test
    public void invalidEmail() throws IOException, InterruptedException {
        User user = User.builder()
                .login("login")
                .email("user!ya.ru")
                .name("Name")
                .birthday(LocalDate.of(1998, 1,1))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user), UTF_8))
                .uri((URI.create("http://localhost:8080/users/")))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
    }

    @Test
    public void emptyName() throws IOException, InterruptedException {
        User user = User.builder()
                .login("login")
                .email("user@ya.ru")
                .birthday(LocalDate.of(1998, 1,1))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user), UTF_8))
                .uri((URI.create("http://localhost:8080/users/")))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
    }

    @Test
    public void invalidBirthday() throws IOException, InterruptedException {
        User user = User.builder()
                .login("login")
                .email("user@ya.ru")
                .name("Name")
                .birthday(LocalDate.of(2030, 1,1))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user), UTF_8))
                .uri((URI.create("http://localhost:8080/users/")))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
    }

}
