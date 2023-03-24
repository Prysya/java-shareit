package ru.practicum.shareit.exception;

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

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestParams(MethodArgumentTypeMismatchException e) {
        String name = e.getName();
        String message;

        var type = e.getRequiredType();

        if (Objects.nonNull(type) && type.isEnum()) {

            // TODO: сделать нормальное сообщение после ревью
            // @remark "Booking get all for user 1 by wrong state" в этом тесте идет проверка на ошибку
            // pm.expect(jsonData.error, 'Error message').to.be.eql('Unknown state: UNSUPPORTED_STATUS');",
            // message = String.format("Параметр '%s' должен иметь значения: %s", name,
            //      Arrays.toString(type.getEnumConstants())
            // );

            message = String.format("Unknown state: %s", e.getValue());
        } else if (Objects.nonNull(type)) {
            message = String.format("Параметр '%s' должен быть типа '%s", name, type.getSimpleName());
        } else {
            message = e.getMessage();
        }

        log.error(message);

        return new ErrorResponse(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        log.error(message);
        return new ErrorResponse(message);
    }


    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final MissingRequestHeaderException e) {
        String message = String.format("Заголовок '%s' не передан'", e.getHeaderName());

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
        List<String> messages = new ArrayList<>();

        ex.getConstraintViolations().forEach(constraintViolation -> messages.add(constraintViolation.getMessage()));

        String message = String.join(", ", messages);

        log.error(message);
        return new ErrorResponse(message);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        String message = e.getMessage();

        log.error(message);
        log.error(String.valueOf(e.getClass()));

        return new ErrorResponse("Произошла непредвиденная ошибка");
    }

    @RequiredArgsConstructor
    @Getter
    private static class ErrorResponse {
        private final String error;
    }
}
