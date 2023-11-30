package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exceptions.InappropriateTimeException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.sql.Timestamp;
import java.time.Instant;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody BookItemRequestDto bookingDto) {
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
        return bookingClient.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable(name = "bookingId") long bookingId,
                                                 @RequestParam(name = "approved") Boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable(name = "bookingId") long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookersBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "Номер запроса с которого начнется страница должен быть больше 0") int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "Размер страницы должен быть больше 0") int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        return bookingClient.getBookersBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnersBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "Номер запроса с которого начнется страница должен быть больше 0") int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "Размер страницы должен быть больше 0") int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        return bookingClient.getOwnersBookings(userId, state, from, size);
    }
}
