package ru.practicum.shareit.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final BadRequestException e) {
        String message =
            e.getMessage();

        log.error(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleBadRequest(final ConflictException e) {
        String message =
            e.getMessage();

        log.error(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadRequest(final UnauthorizedException e) {
        String message =
            e.getMessage();

        log.error(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        String message = e.getMessage();

        log.error(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final MethodArgumentNotValidException e) {
        String message = "Поле " + Objects.requireNonNull(e.getBindingResult().getFieldError()).getField() +
            " " +
            e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        log.error(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final MethodArgumentTypeMismatchException e) {
        String message =
            String.format("Параметр '%s' со значением '%s' не может быть приведен к типу '%s'", e.getName(),
                e.getValue(), Objects.requireNonNull(
                    e.getRequiredType()).getSimpleName()
            );

        log.error(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final MissingRequestHeaderException e) {
        String message =
            String.format("Заголовок '%s' не передан'", e.getHeaderName());

        log.error(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final MissingPathVariableException e) {
        String message = String.format("Параметр: '%s' не передан", e.getVariableName());

        log.error(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final ConstraintViolationException ex) {
        String message = ex.getMessage();

        log.error(message);
        return new ErrorResponse(message);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        String message = e.getMessage();

        log.error(message);
        log.error(String.valueOf(e.getClass()));

        return new ErrorResponse(
            "Произошла непредвиденная ошибка"
        );
    }

    @RequiredArgsConstructor
    @Getter
    private static class ErrorResponse {
        private final String error;
    }
}
