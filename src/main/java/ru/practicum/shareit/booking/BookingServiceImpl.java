package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public BookingDto addBooking(BookingDto bookingDto) {
        return null;
    }

    @Override
    public BookingDto updateBooking(long userId, long itemId, BookingDto bookingDto) {
        return null;
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        return null;
    }

    @Override
    public void approveBooking(long userId, boolean approved) {

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
