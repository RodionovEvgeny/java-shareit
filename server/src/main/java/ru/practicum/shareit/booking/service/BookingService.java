package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(long userId, BookingDto bookingDto);

    BookingDto getBookingById(long userId, long bookingId);

    BookingDto approveBooking(long userId, long bookingId, boolean approved);

    List<BookingDto> getOwnersBookings(long userId, String state, int from, int size);

    List<BookingDto> getBookersBookings(long userId, String state, int from, int size);
}
