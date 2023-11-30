package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class RequestServiceImplIntegratedTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestService requestService;
    @Autowired
    private ItemService itemService;

    @Test
    void getOwnersItemRequests() {
        User user1 = User.builder()
                .id(1L)
                .email("Email@email.com")
                .name("Name")
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("Email2@email.com")
                .name("Name")
                .build();
        ItemDto itemDto1 = ItemDto.builder()
                .name("name1")
                .description("description")
                .available(Boolean.TRUE)
                .requestId(1L)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .description("Description1")
                .build();
        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .description("Description2")
                .build();

        requestService.addItemRequest(2, itemRequestDto1);
        requestService.addItemRequest(2, itemRequestDto2);

        itemService.addItem(1, itemDto1);

        List<ItemRequestDtoWithAnswers> itemRequestDtos = requestService.getOwnersItemRequests(2);

        assertEquals(2, itemRequestDtos.size());
        assertEquals(2, itemRequestDtos.get(0).getId());
        assertEquals(0, itemRequestDtos.get(0).getItems().size());

        assertEquals(1, itemRequestDtos.get(1).getId());
        assertEquals(1, itemRequestDtos.get(1).getItems().size());
    }
}