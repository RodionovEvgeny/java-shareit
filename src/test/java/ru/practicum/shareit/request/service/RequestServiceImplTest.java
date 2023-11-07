package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(RequestServiceImpl.class)
@AutoConfigureMockMvc
class RequestServiceImplTest {

    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private RequestService requestService;

    @Test
    void addItemRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(createItemRequest());

        ItemRequestDto itemRequestDto = requestService.addItemRequest(anyLong(), createItemRequestDto());

        assertEquals(1, itemRequestDto.getId());
        assertEquals("description", itemRequestDto.getDescription());
    }

    @Test
    void getOwnersItemRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(createItemRequest()));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(new Item()));

        List<ItemRequestDtoWithAnswers> itemRequestDtos = requestService.getOwnersItemRequests(anyLong());

        assertEquals(1, itemRequestDtos.size());
        assertEquals(1, itemRequestDtos.get(0).getId());
        assertEquals("description", itemRequestDtos.get(0).getDescription());
        assertNotNull(itemRequestDtos.get(0).getItems());

    }

    @Test
    void getAllItemRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRequestRepository.findByRequestorIdNot(any(Pageable.class), anyLong()))
                .thenReturn(List.of(createItemRequest()));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(new Item()));


        List<ItemRequestDtoWithAnswers> itemRequestDtos = requestService.getAllItemRequests(anyLong(), 1, 1);

        assertEquals(1, itemRequestDtos.size());
        assertEquals(1, itemRequestDtos.get(0).getId());
        assertEquals("description", itemRequestDtos.get(0).getDescription());
        assertNotNull(itemRequestDtos.get(0).getItems());
    }

    @Test
    void getItemRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(createItemRequest()));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(new Item()));

        ItemRequestDtoWithAnswers itemRequestDto = requestService.getItemRequest(anyLong(), 1);

        assertEquals(1, itemRequestDto.getId());
        assertEquals("description", itemRequestDto.getDescription());
        assertNotNull(itemRequestDto.getItems());
    }

    @Test
    void getItemRequestWithUserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(createItemRequest()));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(new Item()));

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    requestService.getItemRequest(1, 1);
                });
        assertEquals("Пользователь с id = 1 не найден!", exception.getMessage());
    }

    @Test
    void getItemRequestWithItemRequestNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(new Item()));

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    requestService.getItemRequest(1, 1);
                });
        assertEquals("Запрос с id = 1 не найден!", exception.getMessage());
    }

    private ItemRequest createItemRequest() {
        return ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(null)
                .created(LocalDateTime.now())
                .build();
    }

    private ItemRequestDto createItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1)
                .email("Email@email.com")
                .name("Name")
                .build();
    }


}