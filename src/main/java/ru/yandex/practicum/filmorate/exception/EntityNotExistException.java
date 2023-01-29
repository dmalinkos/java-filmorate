package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class EntityNotExistException extends RuntimeException{
    public EntityNotExistException(String massage, String className) {
        super(massage + "|Class: " + className + "|");
    }
}
