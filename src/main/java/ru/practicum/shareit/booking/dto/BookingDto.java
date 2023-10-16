package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDto {
    private long id;
    @Future
    private Timestamp start;
    @Future
    private Timestamp end;
    @NotNull
    private Long item;
    private Long booker;
    private BookingStatus status;
}
