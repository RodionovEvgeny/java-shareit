package ru.practicum.shareit.exceptions;

public class UnknownBookingException extends RuntimeException {
    public UnknownBookingException(String message) {
        super(message);
    }
}