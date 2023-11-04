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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

        BookingDto bookingDto = bookingService.getBookingById(1, 1);

        assertEquals(1, bookingDto.getId());
        assertEquals(1, bookingDto.getItem().getId());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertEquals(1, bookingDto.getBooker().getId());
    }

    @Test
    void getOwnersBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(any(Pageable.class), anyLong()))
                .thenReturn(new PageImpl<>(List.of(createBooking())));

        List<BookingDto> bookingDtos = bookingService.getOwnersBookings(1, "ALL", 0, 10);

        assertEquals(1, bookingDtos.size());
        assertEquals(1, bookingDtos.get(0).getId());
        assertEquals(1, bookingDtos.get(0).getItem().getId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
        assertEquals(1, bookingDtos.get(0).getBooker().getId());
    }

    @Test
    void getBookersBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(bookingRepository.findByBookerIdOrderByStartDesc(any(Pageable.class), anyLong()))
                .thenReturn(new PageImpl<>(List.of(createBooking())));

        List<BookingDto> bookingDtos = bookingService.getBookersBookings(1, "ALL", 0, 10);

        assertEquals(1, bookingDtos.size());
        assertEquals(1, bookingDtos.get(0).getId());
        assertEquals(1, bookingDtos.get(0).getItem().getId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
        assertEquals(1, bookingDtos.get(0).getBooker().getId());

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