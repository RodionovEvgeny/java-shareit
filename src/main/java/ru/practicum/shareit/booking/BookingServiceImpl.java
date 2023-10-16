package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {


    @Override
    public BookingDto addBooking(long userId, BookingDto bookingDto) {
        return null;
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        return null;
    }

    @Override
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        return null;
    }

    @Override
    public List<BookingDto> getOwnersBookings(long userId, State state) {
        return null;
    }

    @Override
    public List<BookingDto> getBookersBookings(long userId, State state) {
        return null;
    }
}
