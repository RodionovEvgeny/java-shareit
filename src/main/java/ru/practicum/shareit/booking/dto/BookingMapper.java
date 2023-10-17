package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, long userId) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(bookingDto.getItemId())
                .booker(userId)
                .status(bookingDto.getStatus().name())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem())
                .booker(booking.getBooker())
                .status(BookingStatus.valueOf(booking.getStatus()))
                .build();
    }

   /* public static List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }*/
}
