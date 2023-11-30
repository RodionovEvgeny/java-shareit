package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({
            InappropriateTimeException.class,
            UnsupportedStatusException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleBadRequestException(final Exception e) {
        log.error(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }
}
