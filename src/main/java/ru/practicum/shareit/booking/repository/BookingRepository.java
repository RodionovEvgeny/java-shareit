package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(long userId);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long userId, String status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long userId);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long userId, String status);

    List<Booking> findByItemIdOrderByStartDesc(long itemId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.start < ?1 " +
            "AND b.item.id = ?2 " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC")
    List<Booking> getPastBookings(LocalDateTime time, Long itemId);


    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.start > ?1 " +
            "AND b.item.id = ?2 " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start ASC")
    List<Booking> getFutureBookings(LocalDateTime time, Long itemId);

    @Query("SELECT COUNT(*) " +
            "FROM Booking AS b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.item.id = ?2 " +
            "AND  b.end < ?3 ")
    int countCompletedBookingByUserIdAndItemId(Long itemId, Long userId, LocalDateTime time);
}