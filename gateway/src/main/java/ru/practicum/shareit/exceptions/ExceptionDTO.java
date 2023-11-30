package ru.practicum.shareit.exceptions;

public class ExceptionDTO {
    private final String error;

    public ExceptionDTO(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
