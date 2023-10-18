package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.InappropriateTimeException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
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
        validateItemById(bookingDto.getItemId());
        validateBookingsDates(bookingDto);
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, user));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        validateUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Бронирование с id = %s не найдено!", bookingId),
                Booking.class.getName()));
        if (userId != booking.getBooker().getId() || userId != itemRepository.findById(booking.getItem()).get().getOwner()) {
            throw new NoAccessException("Просмотр бронирования разрешен только автору или владельцу предмета!");
        } // TODO переписать через связи а то выглядит очень по уродски! Или хотябы в метод вынести получение предмета
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        validateUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Бронирование с id = %s не найдено!", bookingId),
                Booking.class.getName()));
        if (userId != itemRepository.findById(booking.getItem()).get().getOwner()) {
            throw new NoAccessException("Подтверждение бронирования разрешено только владельцу предмета!");
        } // TODO переписать через связи а то выглядит очень по уродски! Или хотябы в метод вынести получение предмета
        if (approved) booking.setStatus(BookingStatus.APPROVED.name());
        else booking.setStatus(BookingStatus.REJECTED.name());
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getOwnersBookings(long userId, State state) {
        //TODO надо таки добавлять связи! А то без них будет выглядеть уродски ;
        return null;
    }

    @Override
    public List<BookingDto> getBookersBookings(long userId, State state) {
        validateUserById(userId);
        switch (state) {
            case ALL:
                return BookingMapper.toBookingDtoList(bookingRepository.findByBookerOrderByStartDesc(userId));
            case PAST:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(userId, Instant.now()));
            case FUTURE:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByBookerAndStartAfterOrderByStartDesc(userId, Instant.now()));
            case CURRENT:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(userId, Instant.now(), Instant.now()));
            case WAITING:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByBookerAndStatusOrderByStartDesc(userId, BookingStatus.WAITING.name()));
            case REJECTED:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByBookerAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED.name()));
        }
        throw new RuntimeException("Что то пошло не так!");
    }

    private User validateUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
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
    }
}
