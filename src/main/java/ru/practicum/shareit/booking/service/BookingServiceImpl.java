package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingAlreadyApprovedException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.InappropriateTimeException;
import ru.practicum.shareit.exceptions.InappropriateUserException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(long userId, BookingDto bookingDto) {
        User user = validateUserById(userId);
        bookingDto.setBooker(user);
        Item item = validateItemById(bookingDto.getItemId());
        if (item.getOwner() == userId) {
            throw new InappropriateUserException("Владелец не может бронировать свою вещь!");
        }
        validateBookingsDates(bookingDto);
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        validateUserById(userId);
        Booking booking = validateBookingById(bookingId);
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner()) {
            throw new NoAccessException("Просмотр бронирования разрешен только автору или владельцу предмета!");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        validateUserById(userId);
        Booking booking = validateBookingById(bookingId);
        if (userId != booking.getItem().getOwner()) {
            throw new NoAccessException("Подтверждение бронирования разрешено только владельцу предмета!");
        }
        if (!BookingStatus.WAITING.name().equals(booking.getStatus())) {
            throw new BookingAlreadyApprovedException("Бронирование уже подтверждено/отменено!");
        }
        if (approved) booking.setStatus(BookingStatus.APPROVED.name());
        else booking.setStatus(BookingStatus.REJECTED.name());
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getOwnersBookings(long userId, String state) {
        validateUserById(userId);
        switch (parseState(state)) {
            case ALL:
                return BookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerOrderByStartDesc(userId));
            case PAST:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByItemOwnerAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()));
            case FUTURE:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByItemOwnerAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()));
            case CURRENT:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now()));
            case WAITING:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(userId, BookingStatus.WAITING.name()));
            case REJECTED:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED.name()));
        }
        throw new RuntimeException("Неизвестная ошибка при получении списка бронирований!");
    }

    @Override
    public List<BookingDto> getBookersBookings(long userId, String state) {
        validateUserById(userId);
        switch (parseState(state)) {
            case ALL:
                return BookingMapper.toBookingDtoList(bookingRepository.findByBookerIdOrderByStartDesc(userId));
            case PAST:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()));
            case FUTURE:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()));
            case CURRENT:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now()));
            case WAITING:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING.name()));
            case REJECTED:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED.name()));
        }
        throw new RuntimeException("Неизвестная ошибка при получении списка бронирований!");
    }

    private User validateUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %s не найден!", userId),
                User.class.getName()));
    }

    private Item validateItemById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Предмет с id = %s не найден!", itemId),
                Item.class.getName()));
        if (!item.getAvailable()) throw new ItemNotAvailableException(
                String.format("Предмет с id = %s недоступен для бронирования!", itemId));
        return item;
    }

    private Booking validateBookingById(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Бронирование с id = %s не найдено!", bookingId),
                Booking.class.getName()));
    }

    private State parseState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(String.format("Unknown state: %s", state));
        }
    }

    private void validateBookingsDates(BookingDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new InappropriateTimeException("Необходимо указать время начала и окончания бронирования!");
        } else if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new InappropriateTimeException("Время начала и окончания бронирования должно отличаться!");
        } else if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new InappropriateTimeException("Время начала бронирования не может быть после окончания бронирования!");
        } else if (bookingDto.getStart().isBefore(Timestamp.from(Instant.now()).toLocalDateTime()) ||
                bookingDto.getEnd().isBefore(Timestamp.from(Instant.now()).toLocalDateTime())) {
            throw new InappropriateTimeException("Время начала и окончания бронирования не могут быть в прошлом!");
        }
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(bookingDto.getItemId()
        );
        for (Booking booking : bookings) {
            if ((bookingDto.getStart().isBefore(booking.getStart()) && bookingDto.getEnd().isAfter(booking.getStart())) ||
                    (bookingDto.getStart().isAfter(booking.getStart()) && bookingDto.getStart().isBefore(booking.getEnd())) ||
                    (bookingDto.getStart().isEqual(booking.getStart()))
            ) {
                throw new InappropriateTimeException("Выбранное время для бронирования уже занято!");
            }
        }
    }
}
