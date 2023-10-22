package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, long authorId, long itemID) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .author(authorId)
                .item(itemID)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor())
                .item(comment.getItem())
                .build();
    }
}
