package ru.yandex.practicum.filmorate.exception;

public class EntityNotExistException extends RuntimeException{
    public EntityNotExistException(String massage) {
        super(massage);
    }

}
