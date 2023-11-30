package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    private final User owner = User.builder()
            .name("Owner")
            .email("Email1@email.com")
            .build();

    private final User commentator = User.builder()
            .name("Commentator")
            .email("Email2@email.com")
            .build();

    private final Item item = Item.builder()
            .name("Name")
            .description("Description")
            .available(Boolean.TRUE)
            .owner(owner)
            .build();

    private final Comment comment1 = Comment.builder()
            .text("Text1")
            .author(commentator)
            .item(item)
            .created(LocalDateTime.now().minusDays(1))
            .build();

    private final Comment comment2 = Comment.builder()
            .text("Text2")
            .author(commentator)
            .item(item)
            .created(LocalDateTime.now().minusDays(2))
            .build();

    @BeforeEach
    void beforeEach() {
        userRepository.save(owner);
        userRepository.save(commentator);
        itemRepository.save(item);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
    }


    @Test
    void findByItemId() {
        List<Comment> comments = commentRepository.findByItemId(item.getId());

        assertEquals(2, comments.size());

        assertEquals("Text1", comments.get(0).getText());
        assertEquals(item, comments.get(0).getItem());
        assertEquals(commentator, comments.get(0).getAuthor());

        assertEquals("Text2", comments.get(1).getText());
        assertEquals(item, comments.get(1).getItem());
        assertEquals(commentator, comments.get(1).getAuthor());
    }
}