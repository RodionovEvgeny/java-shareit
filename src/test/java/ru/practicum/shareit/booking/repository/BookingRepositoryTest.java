package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private User owner = User.builder()
            .name("Owner")
            .email("Email1@email.com")
            .build();

    private User booker = User.builder()
            .name("Booker")
            .email("Email2@email.com")
            .build();

    private Item item = Item.builder()
            .name("Name")
            .description("Description")
            .available(Boolean.TRUE)
            .owner(owner)
            .build();

    private Booking booking = Booking.builder()
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(3))
            .item(item)
            .booker(booker)
            .status(BookingStatus.APPROVED.name())
            .build();

    @BeforeEach
    void beforeEach() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    void findByBookerIdOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(Pageable.unpaged(), booker.getId());

        assertEquals(1, bookings.getContent().size());
        assertEquals(booker, bookings.getContent().get(0).getBooker());
        assertEquals(item, bookings.getContent().get(0).getItem());
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                Pageable.unpaged(), booker.getId(), LocalDateTime.now());

        assertEquals(0, bookings.getContent().size());

        bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                Pageable.unpaged(), booker.getId(), LocalDateTime.now().plusDays(4));

        assertEquals(1, bookings.getContent().size());
        assertEquals(booker, bookings.getContent().get(0).getBooker());
        assertEquals(item, bookings.getContent().get(0).getItem());
    }

    @Test
    void findByBookerIdAndStartAfterOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                Pageable.unpaged(), booker.getId(), LocalDateTime.now().plusDays(4));

        assertEquals(0, bookings.getContent().size());

        bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                Pageable.unpaged(), booker.getId(), LocalDateTime.now());

        assertEquals(1, bookings.getContent().size());
        assertEquals(booker, bookings.getContent().get(0).getBooker());
        assertEquals(item, bookings.getContent().get(0).getItem());
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                Pageable.unpaged(), booker.getId(), LocalDateTime.now(), LocalDateTime.now());

        assertEquals(0, bookings.getContent().size());

        bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                Pageable.unpaged(), booker.getId(), LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2));

        assertEquals(1, bookings.getContent().size());
        assertEquals(booker, bookings.getContent().get(0).getBooker());
        assertEquals(item, bookings.getContent().get(0).getItem());
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                Pageable.unpaged(), booker.getId(), BookingStatus.REJECTED.name());

        assertEquals(0, bookings.getContent().size());

        bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                Pageable.unpaged(), booker.getId(), BookingStatus.APPROVED.name());

        assertEquals(1, bookings.getContent().size());
        assertEquals(booker, bookings.getContent().get(0).getBooker());
        assertEquals(item, bookings.getContent().get(0).getItem());
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(Pageable.unpaged(), owner.getId());

        assertEquals(1, bookings.getContent().size());
        assertEquals(booker, bookings.getContent().get(0).getBooker());
        assertEquals(item, bookings.getContent().get(0).getItem());
    }

    @Test
    void findByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                Pageable.unpaged(), owner.getId(), LocalDateTime.now());

        assertEquals(0, bookings.getContent().size());

        bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                Pageable.unpaged(), owner.getId(), LocalDateTime.now().plusDays(4));

        assertEquals(1, bookings.getContent().size());
        assertEquals(booker, bookings.getContent().get(0).getBooker());
        assertEquals(item, bookings.getContent().get(0).getItem());
    }

    @Test
    void findByItemOwnerIdAndStartAfterOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                Pageable.unpaged(), owner.getId(), LocalDateTime.now().plusDays(4));

        assertEquals(0, bookings.getContent().size());

        bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                Pageable.unpaged(), owner.getId(), LocalDateTime.now());

        assertEquals(1, bookings.getContent().size());
        assertEquals(booker, bookings.getContent().get(0).getBooker());
        assertEquals(item, bookings.getContent().get(0).getItem());
    }

    @Test
    void findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                Pageable.unpaged(), owner.getId(), LocalDateTime.now(), LocalDateTime.now());

        assertEquals(0, bookings.getContent().size());

        bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                Pageable.unpaged(), owner.getId(), LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2));

        assertEquals(1, bookings.getContent().size());
        assertEquals(booker, bookings.getContent().get(0).getBooker());
        assertEquals(item, bookings.getContent().get(0).getItem());
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                Pageable.unpaged(), owner.getId(), BookingStatus.REJECTED.name());

        assertEquals(0, bookings.getContent().size());

        bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                Pageable.unpaged(), owner.getId(), BookingStatus.APPROVED.name());

        assertEquals(1, bookings.getContent().size());
        assertEquals(booker, bookings.getContent().get(0).getBooker());
        assertEquals(item, bookings.getContent().get(0).getItem());
    }

    @Test
    void findByItemIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(item.getId());

        assertEquals(1, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
        assertEquals(item, bookings.get(0).getItem());
    }

    @Test
    void getPastBookings() {
        List<Booking> bookings = bookingRepository.getPastBookings(LocalDateTime.now(), item.getId());
        assertEquals(0, bookings.size());

        bookings = bookingRepository.getPastBookings(LocalDateTime.now().plusDays(5), item.getId());

        assertEquals(1, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
        assertEquals(item, bookings.get(0).getItem());
    }

    @Test
    void getFutureBookings() {
        List<Booking> bookings = bookingRepository.getFutureBookings(LocalDateTime.now().plusDays(5), item.getId());
        assertEquals(0, bookings.size());

        bookings = bookingRepository.getFutureBookings(LocalDateTime.now(), item.getId());

        assertEquals(1, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
        assertEquals(item, bookings.get(0).getItem());
    }

    @Test
    void countCompletedBookingByUserIdAndItemId() {
        int count = bookingRepository.countCompletedBookingByUserIdAndItemId(
                booker.getId(), item.getId(), LocalDateTime.now());
        assertEquals(0, count);

        count = bookingRepository.countCompletedBookingByUserIdAndItemId(
                booker.getId(), item.getId(), LocalDateTime.now().plusDays(10));
        assertEquals(1, count);
    }
}