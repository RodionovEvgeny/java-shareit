package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        validateUserById(userId);
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        validateUserById(userId);
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Предмет с id = %s не найден!", itemId),
                Item.class.getName()));
        if (updatedItem.getOwner() != userId)
            throw new NoAccessException("Редактировать предмет может только его хозяин!");
        if (itemDto.getName() != null) updatedItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) updatedItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) updatedItem.setAvailable(itemDto.getAvailable());
        return ItemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public ItemDtoWithBookings getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Предмет с id = %s не найден!", itemId),
                Item.class.getName()));
        if (userId == item.getOwner()) {
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
        return ItemMapper.toItemDtoWithBookings(item, null, null);
    }

    @Override
    public List<ItemDtoWithBookings> getOwnersItems(long userId) {
        List<ItemDtoWithBookings> itemDtoWithBookings = new ArrayList<>();
        validateUserById(userId);
        List<Item> items = itemRepository.findByOwner(userId);
        for (Item item : items) {
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
            itemDtoWithBookings.add(ItemMapper.toItemDtoWithBookings(item, nextBooking, lastBooking));
        }
        return itemDtoWithBookings;
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return ItemMapper.toItemDtoList(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text));
    }

    private void validateUserById(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %s не найден!", userId),
                User.class.getName()));
    }
}
