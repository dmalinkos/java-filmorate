package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomAdvice {

    @ExceptionHandler(EntityNotExistException.class)
    public ResponseEntity<?> handleNotExistException(EntityNotExistException e) {
        String message = String.format("{\"message\":\"%s\"}", e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

}
