package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(owner)
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    public static ItemDto toItemDtoWithBookings(Item item, BookingDto nextBooking, BookingDto lastBooking) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .nextBooking(nextBooking)
                .lastBooking(lastBooking)
                .build();
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
