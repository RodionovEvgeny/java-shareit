package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleNotFoundException(final EntityNotFoundException e) {
        log.warn(String.format("Не найден объект класса %s.", e.getNotFoundClassName()));
        log.warn(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleNoAccessException(final NoAccessException e) {
        log.warn(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDTO handleEmailAlreadyExistsException(final EmailAlreadyExistsException e) {
        log.warn(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleItemNotAvailableException(final ItemNotAvailableException e) {
        log.warn(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleInappropriateTimeException(final InappropriateTimeException e) {
        log.warn(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleUnsupportedStatusException(final UnsupportedStatusException e) {
        log.warn(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleBookingAlreadyApprovedException(final BookingAlreadyApprovedException e) {
        log.warn(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleInappropriateUserException(final InappropriateUserException e) {
        log.warn(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }
}
