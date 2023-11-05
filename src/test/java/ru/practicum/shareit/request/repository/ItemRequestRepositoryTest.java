package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1 = User.builder()
            .name("User1")
            .email("Email1@email.com")
            .build();
    private User user2 = User.builder()
            .name("User2")
            .email("Email2@email.com")
            .build();
    private ItemRequest itemRequest1 = ItemRequest.builder()
            .requestor(user1)
            .description("request1")
            .created(LocalDateTime.now().plusDays(5))
            .build();
    private ItemRequest itemRequest2 = ItemRequest.builder()
            .requestor(user1)
            .description("request2")
            .created(LocalDateTime.now().plusDays(4))
            .build();
    private ItemRequest itemRequest3 = ItemRequest.builder()
            .requestor(user2)
            .description("request3")
            .created(LocalDateTime.now().plusDays(3))
            .build();

    @BeforeEach
    void beforeEach() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
        itemRequestRepository.save(itemRequest3);

    }

    @Test
    void findByRequestorIdOrderByCreatedDesc() {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user1.getId());

        assertEquals(2, itemRequests.size());

        assertEquals("request1", itemRequests.get(0).getDescription());
        assertEquals(user1, itemRequests.get(0).getRequestor());

        assertEquals("request2", itemRequests.get(1).getDescription());
        assertEquals(user1, itemRequests.get(1).getRequestor());
    }

    @Test
    void findByRequestorIdNot() {
        Page<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdNot(Pageable.unpaged(), user2.getId());

        assertEquals(2, itemRequests.getContent().size());

        assertEquals("request1", itemRequests.getContent().get(0).getDescription());
        assertEquals(user1, itemRequests.getContent().get(0).getRequestor());

        assertEquals("request2", itemRequests.getContent().get(1).getDescription());
        assertEquals(user1, itemRequests.getContent().get(1).getRequestor());
    }
}