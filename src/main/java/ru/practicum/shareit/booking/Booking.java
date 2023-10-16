package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "bookings")
@Data
@Builder
public class Booking {
    @Id
    private long id;
    private Timestamp start;
    private Timestamp end;
    private Long item;
    private Long booker;
    private BookingStatus status;

    @Tolerate
    public Booking() {
    }
}
