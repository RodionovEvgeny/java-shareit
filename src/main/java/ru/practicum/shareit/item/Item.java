package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
@Builder
public class Item {
    private long id;
    private String name;
    private String description;
    private Long owner;
    private Boolean available;
    private ItemRequest request;
}
