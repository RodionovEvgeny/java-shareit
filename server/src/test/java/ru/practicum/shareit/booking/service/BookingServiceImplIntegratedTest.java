package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BookingServiceImplIntegratedTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingService bookingService;

    @Test
    void getBookersBookings() {
        User user1 = User.builder()
                .email("Email@email.com")
                .name("Name")
                .build();
        User user2 = User.builder()
                .email("Email2@email.com")
                .name("Name")
                .build();
        Item item1 = Item.builder()
                .name("name1")
                .description("description")
                .available(Boolean.TRUE)
                .owner(user1)
                .build();
        Item item2 = Item.builder()
                .name("name2")
                .description("description")
                .available(Boolean.TRUE)
                .owner(user1)
                .build();
        Item item3 = Item.builder()
                .name("name3")
                .description("description")
                .available(Boolean.TRUE)
                .owner(user1)
                .build();

        BookingDto bookingDto1 = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto bookingDto2 = BookingDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build();
        BookingDto bookingDto3 = BookingDto.builder()
                .itemId(3L)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(6))
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        bookingService.addBooking(2, bookingDto1);
        bookingService.addBooking(2, bookingDto2);
        bookingService.addBooking(2, bookingDto3);

        List<BookingDto> bookingDtos = bookingService.getBookersBookings(2, "ALL", 0, 10);

        assertEquals(3, bookingDtos.size());
        assertEquals(3, bookingDtos.get(0).getId());
        assertEquals(3, bookingDtos.get(0).getItem().getId());
        assertEquals(2, bookingDtos.get(0).getBooker().getId());

        assertEquals(2, bookingDtos.get(1).getId());
        assertEquals(2, bookingDtos.get(1).getItem().getId());
        assertEquals(2, bookingDtos.get(1).getBooker().getId());

        assertEquals(1, bookingDtos.get(2).getId());
        assertEquals(1, bookingDtos.get(2).getItem().getId());
        assertEquals(2, bookingDtos.get(2).getBooker().getId());
    }
}