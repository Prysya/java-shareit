package ru.practicum.shareit.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String errorMessage) {
        super(errorMessage);
    }
}
