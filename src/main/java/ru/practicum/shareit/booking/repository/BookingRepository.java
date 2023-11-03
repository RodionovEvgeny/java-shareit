package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerIdOrderByStartDesc(Pageable pageable, long userId);

    Page<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Pageable pageable, long userId, LocalDateTime now);

    Page<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Pageable pageable, long userId, LocalDateTime now);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Pageable pageable, long userId, LocalDateTime now, LocalDateTime now2);

    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(Pageable pageable, long userId, String status);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(Pageable pageable, long userId);

    Page<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Pageable pageable, long userId, LocalDateTime now);

    Page<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Pageable pageable, long userId, LocalDateTime now);

    Page<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Pageable pageable, long userId, LocalDateTime now, LocalDateTime now2);

    Page<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Pageable pageable, long userId, String status);

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