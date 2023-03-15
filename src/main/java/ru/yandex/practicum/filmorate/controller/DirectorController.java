package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Получен POST запрос к эндпоинту /directors на добавление нового режиссера.");
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Получен POST запрос к эндпоинту /directors на обновление режиссера.");
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public Director delete(@PathVariable("id") Long directorId) {
        return directorService.delete(directorId);
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable("id") Long directorId) {
        log.info(String.format("Получен GET запрос к эндпоинту /directors на получение режиссера с ID:%d.",
                directorId));
        return directorService.getDirector(directorId);
    }

    @GetMapping
    public Collection<Director> getDirectors() {
        log.info("Получен GET запрос к эндпоинту /directors на получение всех режиссеров.");
        return directorService.getDirectors();
    }
}
