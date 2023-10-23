package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import ru.practicum.shareit.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    @ManyToOne
    @JoinColumn(name = "author")
    private User author;
    @ManyToOne
    @JoinColumn(name = "item")
    private Item item;
    private LocalDateTime created;

    @Tolerate
    public Comment() {
    }
}
