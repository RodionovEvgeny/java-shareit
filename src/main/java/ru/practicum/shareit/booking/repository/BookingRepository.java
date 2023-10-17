package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerOrderByStartDesc(long userId);

    List<Booking> findByBookerAndEndBeforeOrderByStartDesc(long userId, Instant now);

    List<Booking> findByBookerAndStartAfterOrderByStartDesc(long userId, Instant now);

    List<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(long userId, Instant now, Instant now2);

    List<Booking> findByBookerAndStatusOrderByStartDesc(long userId, String status);



}