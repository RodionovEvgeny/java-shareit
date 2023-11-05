package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.item.model.Item;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleSimpleNotFoundException() {
        ExceptionDTO exceptionDTO = errorHandler.handleSimpleNotFoundException(
                new InappropriateUserException("Text"));
        assertEquals("Text", exceptionDTO.getError());

        exceptionDTO = errorHandler.handleSimpleNotFoundException(
                new NoAccessException("Text"));
        assertEquals("Text", exceptionDTO.getError());
    }

    @Test
    void handleEntityNotFoundException() {
        ExceptionDTO exceptionDTO = errorHandler.handleEntityNotFoundException(
                new EntityNotFoundException("Text", Item.class.getName()));
        assertEquals("Text", exceptionDTO.getError());
    }

    @Test
    void handleBadRequestException() {
        ExceptionDTO exceptionDTO = errorHandler.handleBadRequestException(
                new ItemNotAvailableException("Text"));
        assertEquals("Text", exceptionDTO.getError());
        exceptionDTO = errorHandler.handleBadRequestException(
                new InappropriateTimeException("Text"));
        assertEquals("Text", exceptionDTO.getError());
        exceptionDTO = errorHandler.handleBadRequestException(
                new UnsupportedStatusException("Text"));
        assertEquals("Text", exceptionDTO.getError());
        exceptionDTO = errorHandler.handleBadRequestException(
                new BookingAlreadyApprovedException("Text"));
        assertEquals("Text", exceptionDTO.getError());
        exceptionDTO = errorHandler.handleBadRequestException(
                new InappropriateCommentException("Text"));
        assertEquals("Text", exceptionDTO.getError());
    }

    @Test
    void handleConflictException() {
        ExceptionDTO exceptionDTO = errorHandler.handleConflictException(
                new EmailAlreadyExistsException("Text"));
        assertEquals("Text", exceptionDTO.getError());
    }

    @Test
    void handleInternalServerErrorException() {
        ExceptionDTO exceptionDTO = errorHandler.handleInternalServerErrorException(
                new UnknownBookingException("Text"));
        assertEquals("Text", exceptionDTO.getError());
    }
}