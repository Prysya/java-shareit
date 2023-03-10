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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestParams(MethodArgumentTypeMismatchException e) {
        String name = e.getName();
        String message;

        var type = e.getRequiredType();

        if (Objects.nonNull(type) && type.isEnum()) {

            // TODO: ?????????????? ???????????????????? ?????????????????? ?????????? ??????????
            // @remark "Booking get all for user 1 by wrong state" ?? ???????? ?????????? ???????? ???????????????? ???? ????????????
            // pm.expect(jsonData.error, 'Error message').to.be.eql('Unknown state: UNSUPPORTED_STATUS');",
            // message = String.format("???????????????? '%s' ???????????? ?????????? ????????????????: %s", name,
            //      Arrays.toString(type.getEnumConstants())
            // );

            message = String.format("Unknown state: %s", e.getValue());
        } else if (Objects.nonNull(type)) {
            message = String.format("???????????????? '%s' ???????????? ???????? ???????? '%s",
                name, type.getSimpleName()
            );
        } else {
            message = e.getMessage();
        }

        log.error(message);

        return new ErrorResponse(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final MethodArgumentNotValidException e) {
        String message = "???????? " + Objects.requireNonNull(e.getBindingResult().getFieldError()).getField() +
            " " +
            e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        log.error(message);
        return new ErrorResponse(message);
    }


    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final MissingRequestHeaderException e) {
        String message =
            String.format("?????????????????? '%s' ???? ??????????????'", e.getHeaderName());

        log.error(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final MissingPathVariableException e) {
        String message = String.format("????????????????: '%s' ???? ??????????????", e.getVariableName());

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
            "?????????????????? ???????????????????????????? ????????????"
        );
    }

    @RequiredArgsConstructor
    @Getter
    private static class ErrorResponse {
        private final String error;
    }
}
