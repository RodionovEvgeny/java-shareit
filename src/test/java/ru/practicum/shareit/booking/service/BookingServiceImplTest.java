package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingAlreadyApprovedException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(BookingServiceImpl.class)
@AutoConfigureMockMvc
class BookingServiceImplTest {
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;
    @Autowired
    private BookingService bookingService;

    @Test
    void addBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createItem()));
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong())).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenReturn(createBooking());

        BookingDto bookingDto = bookingService.addBooking(anyLong(), createBookingDto());

        assertEquals(1, bookingDto.getId());
        assertEquals(1, bookingDto.getItem().getId());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertEquals(1, bookingDto.getBooker().getId());
    }

    @Test
    void addBookingWithUserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createItem()));
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong())).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenReturn(createBooking());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    bookingService.addBooking(1, createBookingDto());
                });
        assertEquals("Пользователь с id = 1 не найден!", exception.getMessage());
    }

    @Test
    void addBookingWithItemNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong())).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenReturn(createBooking());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    bookingService.addBooking(1, createBookingDto());
                });
        assertEquals("Предмет с id = 1 не найден!", exception.getMessage());
    }

    @Test
    void getBookingById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(createBooking()));

        BookingDto bookingDto = bookingService.getBookingById(1, 1);

        assertEquals(1, bookingDto.getId());
        assertEquals(1, bookingDto.getItem().getId());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertEquals(1, bookingDto.getBooker().getId());
    }

    @Test
    void approveBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(createBooking()));
        when(bookingRepository.save(any(Booking.class))).thenReturn(createBooking());

        BookingDto bookingDto = bookingService.approveBooking(1, 1, Boolean.TRUE);

        assertEquals(1, bookingDto.getId());
        assertEquals(1, bookingDto.getItem().getId());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertEquals(1, bookingDto.getBooker().getId());
    }

    @Test
    void approveBookingWithExceptions() {
        Booking booking = createBooking();
        booking.setStatus(BookingStatus.APPROVED.name());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Exception exception = assertThrows(
                NoAccessException.class,
                () -> {
                    bookingService.approveBooking(99, 1, Boolean.TRUE);
                });
        assertEquals("Подтверждение бронирования разрешено только владельцу предмета!", exception.getMessage());

        exception = assertThrows(
                BookingAlreadyApprovedException.class,
                () -> {
                    bookingService.approveBooking(1, 1, Boolean.TRUE);
                });
        assertEquals("Бронирование уже подтверждено/отменено!", exception.getMessage());
    }

    @Test
    void getOwnersBookings() {
        Booking booking1 = createBooking();
        Booking booking2 = createBooking();
        Booking booking3 = createBooking();
        Booking booking4 = createBooking();
        Booking booking5 = createBooking();
        booking1.setId(1);
        booking2.setId(2);
        booking3.setId(3);
        booking4.setId(4);
        booking5.setId(5);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(
                any(Pageable.class), anyLong()))
                .thenReturn(new PageImpl<>(List.of(booking1)));
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                any(Pageable.class), anyLong(), any(LocalDateTime.class)))
                .thenReturn(new PageImpl<>(List.of(booking2)));
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                any(Pageable.class), anyLong(), any(LocalDateTime.class)))
                .thenReturn(new PageImpl<>(List.of(booking3)));
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                any(Pageable.class), anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new PageImpl<>(List.of(booking4)));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                any(Pageable.class), anyLong(), anyString()))
                .thenReturn(new PageImpl<>(List.of(booking5)));

        List<BookingDto> bookingDtos = bookingService.getOwnersBookings(1, "ALL", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(1, bookingDtos.get(0).getId());
        assertEquals(1, bookingDtos.get(0).getItem().getId());

        bookingDtos = bookingService.getOwnersBookings(1, "PAST", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(2, bookingDtos.get(0).getId());

        bookingDtos = bookingService.getOwnersBookings(1, "FUTURE", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(3, bookingDtos.get(0).getId());

        bookingDtos = bookingService.getOwnersBookings(1, "CURRENT", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(4, bookingDtos.get(0).getId());

        bookingDtos = bookingService.getOwnersBookings(1, "WAITING", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(5, bookingDtos.get(0).getId());

        bookingDtos = bookingService.getOwnersBookings(1, "REJECTED", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(5, bookingDtos.get(0).getId());

        Exception exception = assertThrows(
                UnsupportedStatusException.class,
                () -> {
                    bookingService.getOwnersBookings(1, "badstate", 0, 10);
                });
        assertEquals("Unknown state: badstate", exception.getMessage());

    }

    @Test
    void getBookersBookings() {
        Booking booking1 = createBooking();
        Booking booking2 = createBooking();
        Booking booking3 = createBooking();
        Booking booking4 = createBooking();
        Booking booking5 = createBooking();
        booking1.setId(1);
        booking2.setId(2);
        booking3.setId(3);
        booking4.setId(4);
        booking5.setId(5);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(bookingRepository.findByBookerIdOrderByStartDesc(
                any(Pageable.class), anyLong()))
                .thenReturn(new PageImpl<>(List.of(booking1)));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                any(Pageable.class), anyLong(), any(LocalDateTime.class)))
                .thenReturn(new PageImpl<>(List.of(booking2)));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                any(Pageable.class), anyLong(), any(LocalDateTime.class)))
                .thenReturn(new PageImpl<>(List.of(booking3)));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                any(Pageable.class), anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new PageImpl<>(List.of(booking4)));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                any(Pageable.class), anyLong(), anyString()))
                .thenReturn(new PageImpl<>(List.of(booking5)));

        List<BookingDto> bookingDtos = bookingService.getBookersBookings(1, "ALL", 0, 10);

        assertEquals(1, bookingDtos.size());
        assertEquals(1, bookingDtos.get(0).getId());
        assertEquals(1, bookingDtos.get(0).getItem().getId());

        bookingDtos = bookingService.getBookersBookings(1, "PAST", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(2, bookingDtos.get(0).getId());

        bookingDtos = bookingService.getBookersBookings(1, "FUTURE", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(3, bookingDtos.get(0).getId());

        bookingDtos = bookingService.getBookersBookings(1, "CURRENT", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(4, bookingDtos.get(0).getId());

        bookingDtos = bookingService.getBookersBookings(1, "WAITING", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(5, bookingDtos.get(0).getId());

        bookingDtos = bookingService.getBookersBookings(1, "REJECTED", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(5, bookingDtos.get(0).getId());
    }

    private Item createItem() {
        return Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(Boolean.TRUE)
                .request(createItemRequest())
                .owner(createUser())
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1)
                .email("Email@email.com")
                .name("Name")
                .build();
    }

    private ItemRequest createItemRequest() {
        return ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(null)
                .created(LocalDateTime.now())
                .build();
    }

    private Booking createBooking() {
        return Booking.builder()
                .id(1L)
                .booker(createUser())
                .item(createItem())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING.name())
                .build();
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }
}