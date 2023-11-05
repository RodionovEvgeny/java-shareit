package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
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

    private ItemRequest itemRequest = ItemRequest.builder()
            .requestor(user2)
            .description("request")
            .created(LocalDateTime.now())
            .build();
    private Item item2 = Item.builder()
            .name("Name2")
            .description("Motorcycle")
            .available(Boolean.TRUE)
            .owner(user1)
            .request(itemRequest)
            .build();
    private Item item1 = Item.builder()
            .name("Name1")
            .description("Car")
            .available(Boolean.TRUE)
            .owner(user1)
            .build();
    private Item item3 = Item.builder()
            .name("car")
            .description("Lamborghini")
            .available(Boolean.TRUE)
            .owner(user2)
            .build();


    @BeforeEach
    void beforeEach() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @Test
    void findByOwnerId() {
        Page<Item> items = itemRepository.findByOwnerId(Pageable.unpaged(), user1.getId());

        assertEquals(2, items.getContent().size());

        assertEquals(user1, items.getContent().get(0).getOwner());
        assertEquals("Name1", items.getContent().get(0).getName());

        assertEquals(user1, items.getContent().get(1).getOwner());
        assertEquals("Name2", items.getContent().get(1).getName());
    }

    @Test
    void findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue() {
        Page<Item> items = itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                Pageable.unpaged(), "car", "car");

        assertEquals(2, items.getContent().size());

        assertEquals(user1, items.getContent().get(0).getOwner());
        assertEquals("Name1", items.getContent().get(0).getName());
        assertEquals("Car", items.getContent().get(0).getDescription());


        assertEquals(user2, items.getContent().get(1).getOwner());
        assertEquals("car", items.getContent().get(1).getName());
        assertEquals("Lamborghini", items.getContent().get(1).getDescription());
    }

    @Test
    void findByRequestId() {
        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());

        assertEquals(1, items.size());
        assertEquals("Name2", items.get(0).getName());
    }
}