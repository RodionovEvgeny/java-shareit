package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(ItemServiceImpl.class)
@AutoConfigureMockMvc
class ItemServiceImplTest {
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private CommentRepository commentRepository;
    @Autowired
    private ItemService itemService;


    @Test
    void addItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(createItemRequest()));
        when((itemRepository.save(any(Item.class)))).thenReturn(createItem());

        ItemDto itemDto = itemService.addItem(anyLong(), createItemDto());

        assertEquals(1, itemDto.getId());
        assertEquals("name", itemDto.getName());
        assertEquals("description", itemDto.getDescription());
        assertEquals(Boolean.TRUE, itemDto.getAvailable());
        assertEquals(1, itemDto.getRequestId());
    }

    @Test
    void updateItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createItem()));
        when((itemRepository.save(any(Item.class)))).thenReturn(createItem());

        ItemDto itemDto = itemService.updateItem(1, 1, createItemDto());

        assertEquals(1, itemDto.getId());
        assertEquals("name", itemDto.getName());
        assertEquals("description", itemDto.getDescription());
        assertEquals(Boolean.TRUE, itemDto.getAvailable());
        assertEquals(1, itemDto.getRequestId());
    }

    @Test
    void getItemById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createItem()));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(createComment()));
        when(bookingRepository.getFutureBookings(any(LocalDateTime.class), anyLong())).thenReturn(List.of(createBooking()));
        when(bookingRepository.getPastBookings(any(LocalDateTime.class), anyLong())).thenReturn(List.of(createBooking()));


        ItemDto itemDto = itemService.getItemById(1, 1);

        assertEquals(1, itemDto.getId());
        assertEquals("name", itemDto.getName());
        assertEquals("description", itemDto.getDescription());
        assertEquals(Boolean.TRUE, itemDto.getAvailable());
        assertEquals(1, itemDto.getRequestId());
        assertEquals(1, itemDto.getComments().size());
        assertEquals(1, itemDto.getLastBooking().getId());
        assertEquals(1, itemDto.getNextBooking().getId());
    }

    @Test
    void getOwnersItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(createComment()));
        when(bookingRepository.getFutureBookings(any(LocalDateTime.class), anyLong())).thenReturn(List.of(createBooking()));
        when(bookingRepository.getPastBookings(any(LocalDateTime.class), anyLong())).thenReturn(List.of(createBooking()));
        when(itemRepository.findByOwnerId(any(Pageable.class), anyLong())).thenReturn(new PageImpl<>(List.of(createItem())));

        List<ItemDto> itemDtos = itemService.getOwnersItems(anyLong(), 0, 10);

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("name", itemDtos.get(0).getName());
        assertEquals("description", itemDtos.get(0).getDescription());
        assertEquals(Boolean.TRUE, itemDtos.get(0).getAvailable());
        assertEquals(1, itemDtos.get(0).getRequestId());
        assertEquals(1, itemDtos.get(0).getComments().size());
        assertEquals(1, itemDtos.get(0).getLastBooking().getId());
        assertEquals(1, itemDtos.get(0).getNextBooking().getId());
    }

    @Test
    void findItems() {
        when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                any(Pageable.class),
                anyString(),
                anyString()))
                .thenReturn(new PageImpl<>(List.of(createItem())));

        List<ItemDto> itemDtos = itemService.findItems("name", 0, 10);

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("name", itemDtos.get(0).getName());
        assertEquals("description", itemDtos.get(0).getDescription());
        assertEquals(Boolean.TRUE, itemDtos.get(0).getAvailable());
        assertEquals(1, itemDtos.get(0).getRequestId());
    }

    @Test
    void addComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createItem()));
        when(commentRepository.save(any(Comment.class))).thenReturn(createComment());
        when(bookingRepository.countCompletedBookingByUserIdAndItemId(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(1);

        CommentDto commentDto = itemService.addComment(1, 1, createCommentDto());

        assertEquals(1, commentDto.getId());
        assertEquals("text", commentDto.getText());
        assertEquals("Name", commentDto.getAuthorName());
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .name("name")
                .description("description")
                .available(Boolean.TRUE)
                .requestId(1L)
                .build();
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

    private Comment createComment() {
        return Comment.builder()
                .id(1L)
                .author(createUser())
                .created(LocalDateTime.now())
                .item(createItem())
                .text("text")
                .build();
    }

    private Booking createBooking() {
        return Booking.builder()
                .id(1L)
                .booker(createUser())
                .item(createItem())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED.name())
                .build();
    }

    private CommentDto createCommentDto() {
        return CommentDto.builder()
                .text("text")
                .build();
    }


}