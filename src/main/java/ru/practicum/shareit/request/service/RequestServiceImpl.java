package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.InappropriateCommentException;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional

    public ItemDto addItem(long userId, ItemDto itemDto) {
        User user = validateUserById(userId);
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
        return ItemMapper.toItemDto(item);
    }

    @Transactional

    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        validateUserById(userId);
        Item updatedItem = validateItemById(itemId);
        if (updatedItem.getOwner().getId() != userId)
            throw new NoAccessException("Редактировать предмет может только его хозяин!");
        if (itemDto.getName() != null) updatedItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) updatedItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) updatedItem.setAvailable(itemDto.getAvailable());
        return ItemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Transactional(readOnly = true)

    public ItemDto getItemById(long userId, long itemId) {
        validateUserById(userId);
        Item item = validateItemById(itemId);
        ItemDto itemDto = userId == item.getOwner().getId() ? addBookingToItem(item) : ItemMapper.toItemDto(item);
        return addCommentsToItemDto(itemDto);
    }

    @Transactional(readOnly = true)

    public List<ItemDto> getOwnersItems(long userId) {
        validateUserById(userId);
        List<Item> items = itemRepository.findByOwnerId(userId);
        return items.stream()
                .map(this::addBookingToItem)
                .map(this::addCommentsToItemDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)

    public List<ItemDto> findItems(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return ItemMapper.toItemDtoList(itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text));
    }

    @Transactional

    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = validateUserById(userId);
        Item item = validateItemById(itemId);
        validateCommentAuthor(userId, item);
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, user, item));
        return CommentMapper.toCommentDto(comment);
    }

    private User validateUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %s не найден!", userId),
                User.class.getName()));
    }

    private Item validateItemById(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Предмет с id = %s не найден!", itemId),
                Item.class.getName()));
    }

    private void validateCommentAuthor(long userId, Item item) {
        int appropriateBookingsCount = bookingRepository.countCompletedBookingByUserIdAndItemId(
                userId,
                item.getId(),
                LocalDateTime.now());

        if (appropriateBookingsCount < 1) {
            throw new InappropriateCommentException("Комментарии могут оставлять только пользователи, " +
                    " бравшие в аренду предмет, после окончания аренды.");
        }
    }

    private ItemDto addBookingToItem(Item item) {
        List<Booking> futureBookings = bookingRepository.getFutureBookings(LocalDateTime.now(), item.getId());
        List<Booking> pastBookings = bookingRepository.getPastBookings(LocalDateTime.now(), item.getId());

        BookingDto nextBooking = futureBookings.isEmpty() ? null : BookingMapper.toBookingDto(futureBookings.get(0));
        BookingDto lastBooking = pastBookings.isEmpty() ? null : BookingMapper.toBookingDto(pastBookings.get(0));
        if (nextBooking != null) {
            nextBooking.setBookerId(nextBooking.getBooker().getId());
        }
        if (lastBooking != null) {
            lastBooking.setBookerId(lastBooking.getBooker().getId());
        }
        return ItemMapper.toItemDtoWithBookings(item, nextBooking, lastBooking);
    }

    private ItemDto addCommentsToItemDto(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findByItemId(itemDto.getId());
        itemDto.setComments(CommentMapper.toCommentDtoList(comments));
        return itemDto;
    }

    @Override
    public ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = validateUserById(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getOwnersItemRequests(long userId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId, int from, int size) {
        return null;
    }

    @Override
    public ItemRequestDto getItemRequest(long itemId) {
        return null;
    }
}
