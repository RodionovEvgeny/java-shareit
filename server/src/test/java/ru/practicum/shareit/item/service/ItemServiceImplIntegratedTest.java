package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ItemServiceImplIntegratedTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemService itemService;


    @Test
    void getOwnersItems() {
        User user1 = User.builder()
                .email("Email@email.com")
                .name("Name")
                .build();
        User user2 = User.builder()
                .email("Email2@email.com")
                .name("Name")
                .build();
        ItemDto itemDto1 = ItemDto.builder()
                .name("name1")
                .description("description")
                .available(Boolean.TRUE)
                .build();
        ItemDto itemDto2 = ItemDto.builder()
                .name("name2")
                .description("description")
                .available(Boolean.TRUE)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        itemService.addItem(1, itemDto1);
        itemService.addItem(1, itemDto2);

        Comment comment = Comment.builder()
                .item(itemRepository.findById(1L).get())
                .created(LocalDateTime.now())
                .author(user2)
                .text("Text")
                .build();

        commentRepository.save(comment);

        Booking booking1 = Booking.builder()
                .item(itemRepository.findById(2L).get())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(userRepository.findById(2L).get())
                .status(BookingStatus.APPROVED.name())
                .build();
        Booking booking2 = Booking.builder()
                .item(itemRepository.findById(2L).get())
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .booker(userRepository.findById(2L).get())
                .status(BookingStatus.APPROVED.name())
                .build();

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<ItemDto> itemDtos = itemService.getOwnersItems(1, 0, 10);

        assertEquals(2, itemDtos.size());

        assertEquals(1, itemDtos.get(0).getId());
        assertEquals(1, itemDtos.get(0).getComments().size());
        assertNull(itemDtos.get(0).getLastBooking());
        assertNull(itemDtos.get(0).getNextBooking());

        assertEquals(2, itemDtos.get(1).getId());
        assertEquals(0, itemDtos.get(1).getComments().size());
        assertNotNull(itemDtos.get(1).getLastBooking());
        assertNotNull(itemDtos.get(1).getNextBooking());
    }
}