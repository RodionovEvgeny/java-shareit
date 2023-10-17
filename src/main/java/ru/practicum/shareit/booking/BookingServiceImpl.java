package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(long userId, BookingDto bookingDto) {
        validateUserById(userId);
        validateItemById(bookingDto.getItemId());
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, userId));
        return BookingMapper.toBookingDto(booking);
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

    private void validateUserById(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %s не найден!", userId),
                User.class.getName()));
    }

    private void validateItemById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Предмет с id = %s не найден!", itemId),
                Item.class.getName()));
        if (!item.getAvailable()) throw new ItemNotAvailableException(
                String.format("Предмет с id = %s недоступен для бронирования!", itemId));
    }
}
