package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({NoAccessException.class,
            InappropriateUserException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleSimpleNotFoundException(final Exception e) {
        log.error(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleEntityNotFoundException(final EntityNotFoundException e) {
        log.error(String.format("Не найден объект класса %s.", e.getNotFoundClassName()));
        log.error(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler({ItemNotAvailableException.class,
            InappropriateTimeException.class,
            UnsupportedStatusException.class,
            BookingAlreadyApprovedException.class,
            InappropriateCommentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleBadRequestException(final Exception e) {
        log.error(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDTO handleConflictException(final Exception e) {
        log.error(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler(UnknownBookingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDTO handleInternalServerErrorException(final Exception e) {
        log.error(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }
}
