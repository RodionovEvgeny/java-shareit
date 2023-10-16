package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDto {
    private long id;
    private Timestamp start;
    private Timestamp end;
    private Long item;
    private Long booker;
    private BookingStatus status;
}
